package ext.library.log;

import com.alibaba.fastjson2.JSONWriter;
import ext.library.constant.HttpAttribute;
import ext.library.convert.Convert;
import ext.library.util.IdUtils;
import ext.library.util.ServletUtils;
import ext.library.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        if (logProperties.isRequest()) {
            long requestTime = System.currentTimeMillis();
            request.setAttribute(HttpAttribute.REQUEST_TIME, requestTime);
        }
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
        LogProperties logProperties = SpringUtils.getBean(LogProperties.class);
        // 打印请求日志
        if (logProperties.isRequest()) {
            long interval = System.currentTimeMillis() - Convert.toLong(request.getAttribute(HttpAttribute.REQUEST_TIME));
            StringJoiner sj = new StringJoiner(" ");
            sj.add(ServletUtils.getClientIP(request)).add(String.valueOf(interval)).add("ms").add(request.getMethod());
            if (ServletUtils.isGetMethod(request)) {
                sj.add(request.getContextPath() + "?" + request.getQueryString());
                log.info(sj.toString());
            } else {
                sj.add(request.getContextPath());
                log.info(sj.toString());
                log.info(ServletUtils.getParamToJson().toString(JSONWriter.Feature.PrettyFormat));
            }
        }
        // MDC 清空
        MDC.clear();
    }
}