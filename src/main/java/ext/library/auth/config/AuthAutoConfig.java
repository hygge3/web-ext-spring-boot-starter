package ext.library.auth.config;

import ext.library.auth.client.UserClient;
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
    public UserClient user() {
        log.info("【Auth 客户端】配置项：{}。Bean：UserClient，执行初始化 ...", AuthProperties.PREFIX);
        return new UserClient();
    }

}
