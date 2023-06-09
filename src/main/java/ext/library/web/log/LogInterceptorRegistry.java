package ext.library.web.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * 日志拦截器注册
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogInterceptorRegistry {

    final LogProperties logProperties;

    /**
     * 添加日志拦截器
     */
    public void registry(InterceptorRegistry registry) {
        if (logProperties != null) {
            boolean logEnabled = logProperties.isEnabled();
            if (logEnabled) {
                log.info("【请求日志】配置项：{}，添加日志拦截器 ...", LogProperties.PREFIX);
                registry.addInterceptor(new LogInterceptor());
            }
        }
    }

}
