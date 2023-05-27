package ext.library.thread.pool;

import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

/**
 * <h2>子线程上下文装饰器</h2>
 * <p><a href="https://stackoverflow.com/questions/23732089/how-to-enable-request-scope-in-async-task-executor">How to enable request scope in async task executor</a></p>
 * <p>传递：RequestAttributes and MDC and SecurityContext</p>
 */
public class ContextDecorator extends AbstractContextDecorator {

    public ContextDecorator(AsyncProperties asyncProperties) {
        super(asyncProperties);
    }

    @Override
    @NonNull
    public Runnable decorate(@NonNull Runnable runnable) {
        // Servlet 上下文
        ServletRequestAttributes context = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        // 日志上下文
        Map<String, String> previous = MDC.getCopyOfContextMap();
        // ServletAsyncContext-enable：异步上下文最长生命周期（最大阻塞父线程多久）
        enableServletAsyncContext(context, asyncProperties);
        return () -> {
            try {
                RequestContextHolder.setRequestAttributes(context);
                if (previous != null) {
                    MDC.setContextMap(previous);
                }
                runnable.run();
            } finally {
                RequestContextHolder.resetRequestAttributes();
                MDC.clear();
                // ServletAsyncContext-complete：完成异步请求处理并关闭响应流
                completeServletAsyncContext(context, asyncProperties);
            }
        };
    }

}