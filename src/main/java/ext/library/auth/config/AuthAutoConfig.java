package ext.library.auth.config;

import ext.library.auth.client.User;
import ext.library.auth.config.properties.AuthProperties;
import ext.library.redis.client.Redis;
import ext.library.redis.config.RedisAutoConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AuthClient 自动配置
 */
@Slf4j
@Configuration
@AutoConfigureAfter(RedisAutoConfig.class)
@EnableConfigurationProperties({AuthProperties.class})
public class AuthAutoConfig {

    @Bean
    @ConditionalOnBean(Redis.class)
    public User user() {
        log.info("【初始化配置-AuthClient-User 客户端】配置项：" + AuthProperties.PREFIX + "。Bean：User ... 已初始化完毕。");
        return new User();
    }

}