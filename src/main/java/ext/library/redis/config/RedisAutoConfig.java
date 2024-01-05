package ext.library.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import ext.library.idempotent.ApiIdempotentController;
import ext.library.redis.client.Redis;
import ext.library.util.SpringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis 自动配置
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@Import(ApiIdempotentController.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisAutoConfig {


    /**
     * <p>支持 FastJson 进行 Redis 存储对象序列/反序列化
     * <p><a href="https://github.com/alibaba/fastjson2/blob/main/docs/spring_support_cn.md">在 Spring 中集成 Fastjson2</a>
     */
    @Bean
    public RedisTemplate<String, Object> extRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 支持Jackson进行Redis存储对象序列/反序列化
        ObjectMapper objectMapper = SpringUtils.getBean(ObjectMapper.class);
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        return redisTemplate;
    }

    @Bean
    @Primary
    @ConditionalOnBean({RedisTemplate.class, StringRedisTemplate.class})
    public Redis redis(@Qualifier("extRedisTemplate") RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
        log.info("【Redis】Bean：Redis。执行初始化 ...");
        return new Redis(redisTemplate, stringRedisTemplate);
    }

}
