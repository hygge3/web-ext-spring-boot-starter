package ext.library.idempotent;

import cn.hutool.extra.servlet.JakartaServletUtil;
import com.alibaba.fastjson2.JSON;
import com.mybatisflex.core.util.ArrayUtil;
import ext.library.exception.ResultException;
import ext.library.redis.client.Redis;
import ext.library.util.ServletUtils;
import ext.library.util.SpringUtils;
import ext.library.util.StringUtils;
import ext.library.util.ThreadLocalUtils;
import ext.library.web.view.Result;
import ext.library.web.view.ResultEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.lang.NonNull;
import org.springframework.util.DigestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * 防止重复提交 (参考美团 GTIS 防重系统)
 *
 * @author Hygge
 * @since 1.0.0
 */
@Aspect
public class RepeatSubmitAspect {
    /**
     * 防重提交 redis key.
     */
    private static final String REPEAT_SUBMIT_KEY = "repeat_submit:";
    private static final String KEY = "repeat";
    private final Redis redis = SpringUtils.getBean(Redis.class);

    @Before("@annotation(repeatSubmit)")
    public void doBefore(JoinPoint point, @NonNull RepeatSubmit repeatSubmit) throws Throwable {
        // 如果注解不为 0 则使用注解数值
        long interval = repeatSubmit.timeUnit().toMillis(repeatSubmit.interval());

        if (interval < 1000) {
            throw new ResultException("重复提交间隔时间不能小于'1'秒");
        }
        HttpServletRequest request = ServletUtils.getRequest();
        String nowParams = argsArrayToString(point.getArgs());

        // 请求地址（作为存放 cache 的 key 值）
        String url = request.getRequestURI();

        // 唯一值（ip）
        String submitKey = StringUtils.trimToEmpty(JakartaServletUtil.getClientIP(request));
        String key = submitKey + ":" + nowParams;
        submitKey = DigestUtils.md5DigestAsHex(key.getBytes(StandardCharsets.UTF_8));
        // 唯一标识（指定 key + url + ip）
        String cacheRepeatKey = REPEAT_SUBMIT_KEY + repeatSubmit.value() + url + submitKey;
        if (redis.setIfAbsent(cacheRepeatKey, "", Duration.ofMillis(interval))) {
            ThreadLocalUtils.put(KEY, cacheRepeatKey);
        } else {
            String message = repeatSubmit.message();
            throw new ResultException(message);
        }
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(repeatSubmit)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, RepeatSubmit repeatSubmit, Object jsonResult) {
        if (jsonResult instanceof Result<?> r) {
            try {
                // 成功则不删除 redis 数据 保证在有效时间内无法重复提交
                if (r.getCode().equals(ResultEnum.SUCCESS.getCode())) {
                    return;
                }
                redis.del(ThreadLocalUtils.get(KEY, String.class));
            } finally {
                ThreadLocalUtils.remove(KEY);
            }
        }
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "@annotation(repeatSubmit)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, RepeatSubmit repeatSubmit, Exception e) {
        redis.del(ThreadLocalUtils.get(KEY, String.class));
        ThreadLocalUtils.remove(KEY);
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        StringJoiner params = new StringJoiner(" ");
        if (ArrayUtil.isEmpty(paramsArray)) {
            return params.toString();
        }
        for (Object o : paramsArray) {
            if (!Objects.isNull(o) && !isFilterObject(o)) {
                params.add(JSON.toJSONString(o));
            }
        }
        return params.toString();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回 true；否则返回 false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(@NonNull final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.values()) {
                return value instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse || o instanceof BindingResult;
    }

}
