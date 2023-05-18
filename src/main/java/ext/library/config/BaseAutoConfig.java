package ext.library.config;

import ext.library.api.version.ApiVersionProperties;
import ext.library.config.datetime.DateTimeFormatConfig;
import ext.library.config.exception.ExceptionHandlerProperties;
import ext.library.config.properties.CorsProperties;
import ext.library.config.thread.pool.AsyncConfig;
import ext.library.util.I18nUtils;
import ext.library.util.SpringUtils;
import ext.library.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * base bean 自动配置
 */
@Slf4j
@Configuration
@Import({SpringUtils.class, I18nUtils.class, AsyncConfig.class, DateTimeFormatConfig.class})
@EnableConfigurationProperties({ApiVersionProperties.class, ExceptionHandlerProperties.class, CorsProperties.class,})
public class BaseAutoConfig {

    // Validator-校验器

    @Bean
    @ConditionalOnMissingBean
    public Validator validator() {
        log.info("【初始化配置 - 校验器】Bean：Validator ... 已初始化完毕。");
        return new Validator();
    }

}