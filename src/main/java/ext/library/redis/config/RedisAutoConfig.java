package ext.library.redis.config;

import ext.library.idempotent.ApiIdempotentController;
import ext.library.redis.client.Redis;
import ext.library.redis.config.properties.RedisProperties;
import ext.library.redis.constant.RedisSerializerEnum;
import ext.library.util.ClassUtils;
import ext.library.util.SpringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * redis 自动配置
 */
@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Import(ApiIdempotentController.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties({RedisProperties.class})
public class RedisAutoConfig {

    final RedisProperties redisProperties;

    /**
     * <p>支持 FastJson 进行 Redis 存储对象序列/反序列化
     * <p><a href="https://github.com/alibaba/fastjson2/blob/main/docs/spring_support_cn.md">在 Spring 中集成 Fastjson2</a>
     */
    @Bean
    public RedisTemplate<String, Object> extRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 支持FastJson进行Redis存储对象序列/反序列化
        if (redisProperties.getRedisSerializer() != RedisSerializerEnum.JDK) {
            redisTemplate.setDefaultSerializer(redisProperties.getRedisSerializer().getRedisSerializer());
        }
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        return redisTemplate;
    }

    @Bean
    @Primary
    @ConditionalOnBean({RedisTemplate.class, StringRedisTemplate.class})
    public Redis redis(@Qualifier("extRedisTemplate") RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
        log.info("【初始化配置-Redis 客户端】配置项：{}，默认使用 {} 进行Redis存储对象序列/反序列化。Bean：Redis ... 已初始化完毕。", RedisProperties.PREFIX, ClassUtils
                .getClassName(RedisSerializerEnum.class, false).concat(".JDK"));
        return new Redis(redisTemplate, stringRedisTemplate);
    }

    @Scheduled(cron = "* 1 * * * *")
    public void heartbeat() {
        SpringUtils.getBean(Redis.class).get("heartbeat");
        log.debug("Redis heartbeat");
    }

}