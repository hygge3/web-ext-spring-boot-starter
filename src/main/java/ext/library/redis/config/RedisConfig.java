package ext.library.redis.config;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import ext.library.redis.handler.KeyPrefixHandler;
import ext.library.redis.manager.FlexSpringCacheManager;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CompositeCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis 配置
 *
 * @author Hygge
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@EnableCaching
@EnableConfigurationProperties(RedisProperties.class)
@RequiredArgsConstructor
public class RedisConfig {
    final RedisProperties redisProperties;
    @Resource
    ObjectMapper objectMapper;

    @Bean
    public RedissonAutoConfigurationCustomizer redissonCustomizer() {
        return config -> {
            ObjectMapper om = objectMapper.copy();
            // 指定要序列化的域，field,get 和 set，以及修饰符范围，ANY 是都有包括 private 和 public
            om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            // 指定序列化输入的类型，类必须是非 final 修饰的。序列化时将对象全类名一起保存下来
            om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
            TypedJsonJacksonCodec jsonCodec = new TypedJsonJacksonCodec(Object.class, om);
            // 组合序列化 key 使用 String 内容使用通用 json 格式
            CompositeCodec codec = new CompositeCodec(StringCodec.INSTANCE, jsonCodec, jsonCodec);
            config.setThreads(redisProperties.getThreads()).setNettyThreads(redisProperties.getNettyThreads()).setCodec(codec);
            RedisProperties.SingleServerConfig singleServerConfig = redisProperties.getSingleServerConfig();
            if (ObjectUtil.isNotNull(singleServerConfig)) {
                // 使用单机模式
                config.useSingleServer()
                    //设置 redis key 前缀
                    .setNameMapper(new KeyPrefixHandler(redisProperties.getKeyPrefix())).setTimeout(singleServerConfig.getTimeout()).setClientName(singleServerConfig.getClientName()).setIdleConnectionTimeout(singleServerConfig.getIdleConnectionTimeout()).setSubscriptionConnectionPoolSize(singleServerConfig.getSubscriptionConnectionPoolSize()).setConnectionMinimumIdleSize(singleServerConfig.getConnectionMinimumIdleSize()).setConnectionPoolSize(singleServerConfig.getConnectionPoolSize());
            }
            // 集群配置方式 参考下方注释
            RedisProperties.ClusterServersConfig clusterServersConfig = redisProperties.getClusterServersConfig();
            if (ObjectUtil.isNotNull(clusterServersConfig)) {
                config.useClusterServers()
                    //设置 redis key 前缀
                    .setNameMapper(new KeyPrefixHandler(redisProperties.getKeyPrefix())).setTimeout(clusterServersConfig.getTimeout()).setClientName(clusterServersConfig.getClientName()).setIdleConnectionTimeout(clusterServersConfig.getIdleConnectionTimeout()).setSubscriptionConnectionPoolSize(clusterServersConfig.getSubscriptionConnectionPoolSize()).setMasterConnectionMinimumIdleSize(clusterServersConfig.getMasterConnectionMinimumIdleSize()).setMasterConnectionPoolSize(clusterServersConfig.getMasterConnectionPoolSize()).setSlaveConnectionMinimumIdleSize(clusterServersConfig.getSlaveConnectionMinimumIdleSize()).setSlaveConnectionPoolSize(clusterServersConfig.getSlaveConnectionPoolSize()).setReadMode(clusterServersConfig.getReadMode()).setSubscriptionMode(clusterServersConfig.getSubscriptionMode());
            }
            log.info("【Redis】初始化序列化配置。");
        };
    }


    /**
     * Redis Template 相关配置
     *
     * @param factory 工厂
     * @return {@link RedisTemplate}<{@link String}, {@link Object}>
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(factory);
        ObjectMapper om = objectMapper.copy();
        // 指定要序列化的域，field,get 和 set，以及修饰符范围，ANY 是都有包括 private 和 public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非 final 修饰的，final 修饰的类，比如 String,Integer 等会跑出异常
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        //使用 Jackson2JsonRedisSerializer 来序列化和反序列化 redis 的 value 值（默认使用 JDK 的序列化方式）
        Jackson2JsonRedisSerializer<Object> jacksonSerializer = new Jackson2JsonRedisSerializer<>(om, Object.class);
        //使用 StringRedisSerializer 来序列化和反序列化 redis 的 key 值
        template.setKeySerializer(new StringRedisSerializer());
        // 值采用 json 序列化
        template.setValueSerializer(jacksonSerializer);
        // 设置 hash key 和 value 序列化模式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jacksonSerializer);
        template.setDefaultSerializer(jacksonSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * String Redis Template 相关配置
     *
     * @param factory 工厂
     * @return {@link StringRedisTemplate}
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(factory);
        return stringRedisTemplate;
    }

    /**
     * 自定义缓存管理器 整合 spring-cache
     */
    @Bean
    public CacheManager cacheManager() {
        return new FlexSpringCacheManager();
    }

}
