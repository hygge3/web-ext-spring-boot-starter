package ext.library.idempotent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * 幂等性拦截器注册
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotentInterceptorRegistry {

    final ApiIdempotentProperties apiIdempotentProperties;

    /**
     * 添加幂等性拦截器
     */
    public void registry(InterceptorRegistry registry) {
        if (apiIdempotentProperties != null) {
            boolean idempotentEnabled = apiIdempotentProperties.isEnabled();
            if (idempotentEnabled) {
                log.info("【幂等】配置项：{}。添加幂等性拦截器 ...", ApiIdempotentProperties.PREFIX);
                registry.addInterceptor(new IdempotentInterceptor());
            }
        }
    }

}
