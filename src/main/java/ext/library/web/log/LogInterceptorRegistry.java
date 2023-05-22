package ext.library.web.log;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * 日志拦截器注册
 */
@Component
@RequiredArgsConstructor
@ConditionalOnClass(LogProperties.class)
@ConditionalOnBean(LogProperties.class)
public class LogInterceptorRegistry {

    final LogProperties logProperties;

    /**
     * 添加日志拦截器
     */
    public void registry(InterceptorRegistry registry) {
        if (logProperties != null) {
            boolean logEnabled = logProperties.isEnabled();
            if (logEnabled) {
                registry.addInterceptor(new LogInterceptor());
            }
        }
    }

}