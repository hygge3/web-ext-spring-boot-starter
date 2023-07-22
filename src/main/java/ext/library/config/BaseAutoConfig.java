package ext.library.config;

import ext.library.thread.pool.AsyncAutoConfig;
import ext.library.util.I18nUtils;
import ext.library.util.SpringUtils;
import ext.library.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * base bean 自动配置
 */
@Slf4j
@Configuration
@Import({SpringUtils.class, I18nUtils.class, AsyncAutoConfig.class})
public class BaseAutoConfig {

    /**
     * Validator-校验器
     *
     * @return {@link Validator}
     */
    @Bean
    @ConditionalOnMissingBean
    public Validator validator() {
        log.info("【校验器】Bean：Validator。执行初始化 ...");
        return new Validator();
    }

}