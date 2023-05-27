package ext.library.thread.pool;

import cn.hutool.core.convert.Convert;
import ext.library.constant.HttpAttribute;
import lombok.AllArgsConstructor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;


/**
 * <h2>子线程上下文装饰器</h2>
 * <p><a href="https://stackoverflow.com/questions/23732089/how-to-enable-request-scope-in-async-task-executor">...</a></p>
 * <p>传递：RequestAttributes and MDC and SecurityContext</p>
 */
@AllArgsConstructor
public abstract class AbstractContextDecorator implements TaskDecorator {

    protected AsyncProperties asyncProperties;

    /**
     * 启用 ServletAsyncContext，异步上下文最长生命周期（最大阻塞父线程多久）
     * <p>用于阻塞父线程 Servlet 的关闭（调用 destroy() 方法），导致子线程获取的上下文为空</p>
     *
     * @param context         父线程上下文
     * @param asyncProperties 异步属性配置
     */
    protected void enableServletAsyncContext(ServletRequestAttributes context, AsyncProperties asyncProperties) {
        if (!asyncProperties.isEnableServletAsyncContext()) {
            return;
        }

        HttpServletRequest request = context.getRequest();
        request.startAsync();
        Object servletAsyncContextTimeoutMillis = request.getAttribute(HttpAttribute.SERVLET_ASYNC_CONTEXT_TIMEOUT_MILLIS);
        if (servletAsyncContextTimeoutMillis == null) {
            servletAsyncContextTimeoutMillis = asyncProperties.getServletAsyncContextTimeoutMillis();
        }

        request.getAsyncContext().setTimeout(Convert.toLong(servletAsyncContextTimeoutMillis));
    }

    /**
     * 完成异步请求处理并关闭响应流
     *
     * @param context         父线程上下文
     * @param asyncProperties 异步属性配置
     */
    protected void completeServletAsyncContext(ServletRequestAttributes context, AsyncProperties asyncProperties) {
        if (asyncProperties.isEnableServletAsyncContext()) {
            context.getRequest().getAsyncContext().complete();
        }
    }

}