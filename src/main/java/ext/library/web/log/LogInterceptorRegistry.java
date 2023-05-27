package ext.library.web.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * 日志拦截器注册
 */
@Slf4j
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
                log.info("【日志】配置项：{}。添加日志拦截器 ...", LogProperties.PREFIX);
                registry.addInterceptor(new LogInterceptor());
            }
        }
    }

}