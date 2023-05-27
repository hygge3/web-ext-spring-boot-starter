package ext.library.thread.pool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.Executor;

/**
 * <b>异步线程池</b>
 * <p>
 * 共用父线程上下文环境，异步执行任务时不丢失 token
 * <p>
 * <b style="color:red">注意，@Async 异步执行方法，不要和同步调用方法，写在同一个类中，否则异步执行将失效。</b>
 */
@Slf4j
@EnableAsync
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(AsyncProperties.class)
@ConditionalOnProperty(prefix = AsyncProperties.PREFIX, name = "enabled", havingValue = "true")
public class AsyncConfig implements AsyncConfigurer {

    final AsyncProperties asyncProperties;
    final TaskDecorator taskDecorator;

    @PostConstruct
    private void init() {
        log.info("【异步线程池】配置项：{}。异步线程池配置已加载，待使用时初始化 ...", AsyncProperties.PREFIX);
    }

    /**
     * 自定义异常处理类
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("Async Exception Method: {}", method);
            int i = 0;
            while (i < params.length) {
                log.error("Method Parameter Value {} - {}", i, params[i]);
                i++;
            }
            log.error("==========================printStackTrace=======================", ex);
        };
    }

    /**
     * 异步线程池<br>
     * 实现 AsyncConfigurer 接口并重写 getAsyncExecutor 方法，返回一个 ThreadPoolTaskExecutor，这样我们就获得了一个基本线程池 TaskExecutor。
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 线程池名的前缀
        executor.setThreadNamePrefix(asyncProperties.getThreadNamePrefix());
        // 核心线程数
        executor.setCorePoolSize(asyncProperties.getCorePoolSize());
        // 最大线程数
        executor.setMaxPoolSize(asyncProperties.getMaxPoolSize());
        // 允许线程的空闲时间
        executor.setKeepAliveSeconds(asyncProperties.getKeepAliveSeconds());
        // 任务队列容量（阻塞队列）
        executor.setQueueCapacity(asyncProperties.getQueueCapacity());
        // 是否允许核心线程超时
        executor.setAllowCoreThreadTimeOut(asyncProperties.isAllowCoreThreadTimeOut());
        // 应用关闭时 - 是否等待未完成任务继续执行，再继续销毁其他的 Bean
        executor.setWaitForTasksToCompleteOnShutdown(asyncProperties.isWaitForTasksToCompleteOnShutdown());
        // 应用关闭时 - 继续等待时间（单位：秒）
        executor.setAwaitTerminationSeconds(asyncProperties.getAwaitTerminationSeconds());
        // 线程池拒绝策略
        executor.setRejectedExecutionHandler(asyncProperties.getRejectedExecutionHandlerPolicy()
                .getRejectedExecutionHandler());
        // 异步线程上下文装饰器
        executor.setTaskDecorator(taskDecorator);
        executor.initialize();
        log.info("【异步线程池】共用父线程上下文环境，异步执行任务时不丢失 token ... 已初始化完毕。");
        return executor;
    }

}