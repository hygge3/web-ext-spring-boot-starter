package ext.library.redis.config.properties;

import ext.library.constant.Constant;
import ext.library.redis.constant.RedisSerializerEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * redis 可配置属性
 */
@Data
@ConfigurationProperties(RedisProperties.PREFIX)
public class RedisProperties {

    /**
     * Prefix of {@link RedisProperties}.
     */
    public static final String PREFIX = Constant.CONFIG_PREFIX + ".redis";

    /**
     * <p>Redis 存储对象序列/反序列化器
     * <p>默认：{@linkplain RedisSerializerEnum#JDK}
     */
    private RedisSerializerEnum redisSerializer = RedisSerializerEnum.JACKSON;

}