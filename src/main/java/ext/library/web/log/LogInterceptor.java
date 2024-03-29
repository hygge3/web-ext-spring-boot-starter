package ext.library.web.log;

import ext.library.constant.HttpAttribute;
import ext.library.convert.Convert;
import ext.library.util.DateUtils;
import ext.library.util.IdUtils;
import ext.library.util.ServletUtils;
import ext.library.util.SpringUtils;
import ext.library.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.StringJoiner;

/**
 * 日志拦截器
 */
@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    /**
     * 在控制器（controller 方法）执行之前
     * <p>执行 traceId 生成并放置请求头
     * <p>记录请求时间
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        LogProperties logProperties = SpringUtils.getBean(LogProperties.class);
        // 请求时间
        long requestTime = System.currentTimeMillis();
        request.setAttribute(HttpAttribute.REQUEST_TIME, requestTime);
        // traceId 生成并放置请求头和 MDC
        if (logProperties.isTrace()) {
            String traceId = IdUtils.objectId();
            response.setHeader(HttpAttribute.TRACE_ID, traceId);
            MDC.put(HttpAttribute.TRACE_ID, traceId);
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) throws Exception {
        // 请求日志 127.0.0.1 5ms GET /text?a=b
        // 打印请求日志
        long interval = System.currentTimeMillis() - Convert.toLong(request.getAttribute(HttpAttribute.REQUEST_TIME));
        StringJoiner sj = new StringJoiner(" ");
        sj.add(ServletUtils.getClientIP(request)).add(DateUtils.formatBetween(interval)).add(request.getMethod()).add(request.getRequestURI());
        log.info(sj + (StringUtils.isNotBlank(request.getQueryString()) ? "?" + request.getQueryString() : ""));
        LogProperties logProperties = SpringUtils.getBean(LogProperties.class);
        if (ServletUtils.hasBodyMethod(request) && logProperties.isBody()) {
            try {
                log.info(ServletUtils.getParamToJson().toString());
            } catch (Exception ignored) {
            }
        }
        // MDC 清空
        MDC.clear();
    }
}
