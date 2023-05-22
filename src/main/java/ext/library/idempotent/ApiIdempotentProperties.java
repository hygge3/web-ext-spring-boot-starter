package ext.library.idempotent;

import ext.library.redis.config.properties.RedisProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 幂等性配置属性
 */
@Data
@ConfigurationProperties(ApiIdempotentProperties.PREFIX)
public class ApiIdempotentProperties {

    /**
     * Prefix of {@link ApiIdempotentProperties}.
     */
    public static final String PREFIX = RedisProperties.PREFIX + "api-idempotent";

    /**
     * 启用接口幂等性
     * <p>默认：false</p>
     */
    boolean enabled = false;

    /**
     * 幂等版本号 Redis 存储失效时间
     */
    int versionTimeout = 300;

}