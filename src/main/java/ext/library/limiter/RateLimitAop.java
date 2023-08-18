package ext.library.limiter;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Limit AOP
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAop {

    private final StringRedisTemplate stringRedisTemplate;


    @Pointcut("@annotation(ext.library.limiter.RateLimit)")
    private void check() {

    }

    @Before("check()")
    public void before(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        //拿到 RateLimit 注解，如果存在则说明需要限流
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        if (rateLimit != null) {
            //获取 redis 的 key
            String key = rateLimit.key();
            String className = method.getDeclaringClass().getName();
            String limitKey;
            limitKey = key + className + method.getName();

            log.info(limitKey);

            if (StrUtil.isEmpty(key)) {
                throw new RateLimitException("key cannot be null");
            }

            long limit = rateLimit.permitsPerSecond();

            long expire = rateLimit.expire();

            List<String> keys = new ArrayList<>();
            keys.add(key);
            String luaScript = buildLuaScript();
            RedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);

            Long count = stringRedisTemplate.execute(redisScript, keys, String.valueOf(limit), String.valueOf(expire));

            log.info("Access try count is {} for key={}", count, key);

            if (count != null && count == 0) {
                log.warn("令牌桶：{}，获取令牌失败", key);
                throw new RateLimitException(rateLimit.msg());
            }
        }

    }

    /**
     * 构建 redis lua 脚本
     *
     * @return {@link String}
     */
    private String buildLuaScript() {
        return """
                local key = KEYS[1]
                local limit = tonumber(ARGV[1])
                local curentLimit = tonumber(redis.call('get', key) or "0")
                if curentLimit + 1 > limit then
                 return 0
                else
                 redis.call("INCRBY", key, 1)
                 redis.call("EXPIRE", key, ARGV[2])
                 return curentLimit + 1
                end
                 """;
    }
}