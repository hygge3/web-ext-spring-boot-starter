package ext.library.idempotent;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * 幂等性拦截器注册
 */
@Component
@RequiredArgsConstructor
@ConditionalOnClass(ApiIdempotentProperties.class)
@ConditionalOnBean(ApiIdempotentProperties.class)
public class IdempotentInterceptorRegistry {

    final ApiIdempotentProperties apiIdempotentProperties;

    /**
     * 添加幂等性拦截器
     */
    public void registry(InterceptorRegistry registry) {
        if (apiIdempotentProperties != null) {
            boolean idempotentEnabled = apiIdempotentProperties.isEnabled();
            if (idempotentEnabled) {
                registry.addInterceptor(new IdempotentInterceptor());
            }
        }
    }

}