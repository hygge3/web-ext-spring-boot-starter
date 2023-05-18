package ext.library.idempotent;

import cn.hutool.core.util.StrUtil;
import ext.library.exception.ParamException;
import ext.library.exception.ResultException;
import ext.library.redis.client.Redis;
import ext.library.redis.constant.RedisConstant;
import ext.library.redis.idempotent.ApiIdempotent;
import ext.library.util.SpringUtils;
import ext.library.view.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 幂等性校验拦截器
 * <p>{@code @ConditionalOnBean(Redis.class)}代码为象征意义上的必须依赖 Redis Bean（因为在这里此注解并不会生效，需在 WebMvcConfigurer 实现类中控制）</p>
 */
@Slf4j
@ConditionalOnBean(Redis.class)
public class IdempotentInterceptor implements HandlerInterceptor {

    /**
     * 在控制器（controller 方法）执行之前
     * <p>执行接口幂等性校验
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Method method = handlerMethod.getMethod();

        // 获取目标方法上的幂等注解
        ApiIdempotent methodAnnotation = method.getAnnotation(ApiIdempotent.class);
        if (methodAnnotation != null) {
            // 幂等性校验，校验通过则放行，校验失败则抛出异常，并通过统一异常处理返回友好提示
            checkApiIdempotent(request);
        }

        return true;
    }

    private void checkApiIdempotent(HttpServletRequest request) {
        String version = request.getHeader(RedisConstant.API_IDEMPOTENT_VERSION_REQUEST_KEY);
        if (StrUtil.isBlank(version)) {
            // header 中不存在 version
            version = request.getParameter(RedisConstant.API_IDEMPOTENT_VERSION_REQUEST_KEY);
            if (StrUtil.isBlank(version)) {
                // parameter 中也不存在 version
                throw new ParamException(StrUtil.format("【幂等性】幂等校验失败，请求中未包含 {} 参数", RedisConstant.API_IDEMPOTENT_VERSION_REQUEST_KEY));
            }
        }

        Redis redis = SpringUtils.getBean(Redis.class);
        String redisKey = RedisConstant.API_IDEMPOTENT_KEY_PREFIX + version;
        if (Objects.equals(Boolean.FALSE, redis.getRedisTemplate().hasKey(redisKey))) {
            String msgPrompt = "请勿重复操作";
            String dataPrompt = StrUtil.format("【幂等性】幂等校验失败，{} 参数已失效，当前 value: {}", RedisConstant.API_IDEMPOTENT_VERSION_REQUEST_KEY, version);
            throw new ResultException(R.errorPrompt(msgPrompt, dataPrompt));
        }

        if (!redis.del(redisKey)) {
            log.warn("【幂等性】幂等校验失败，{} 参数未能正确的解锁", redisKey);
        }
    }

}