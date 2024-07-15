package ext.library.idempotent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConfiguration;

/**
 * 幂等功能配置
 *
 * @author Hygge
 * @since 1.0.0
 */

@Slf4j
@ConditionalOnBean(RedisConfiguration.class)
public class RepeatSubmitConfig {

    @Bean
    public RepeatSubmitAspect repeatSubmitAspect() {
        log.info("【幂等性】添加幂等性功能。");
        return new RepeatSubmitAspect();
    }

}
