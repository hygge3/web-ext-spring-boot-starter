package ext.library.redis.config;

import ext.library.constant.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redis 可配置属性
 *
 * @author Hygge
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(RedisProperties.PREFIX)
public class RedisProperties {

    /**
     * Prefix of {@link RedisProperties}.
     */
    public static final String PREFIX = Constant.CONFIG_PREFIX + ".redis";

    /**
     * redis 缓存 key 前缀
     */
    private String keyPrefix = Constant.CONFIG_PREFIX;

    /**
     * 线程池数量，默认值 = 当前处理核数量 * 2
     */
    private int threads;

    /**
     * Netty 线程池数量，默认值 = 当前处理核数量 * 2
     */
    private int nettyThreads;

    /**
     * 单机服务配置
     */
    private SingleServerConfig singleServerConfig;

    /**
     * 集群服务配置
     */
    private ClusterServersConfig clusterServersConfig;

    @Data
    @NoArgsConstructor
    public static class SingleServerConfig {

        /**
         * 客户端名称
         */
        private String clientName;

        /**
         * 最小空闲连接数
         */
        private int connectionMinimumIdleSize;

        /**
         * 连接池大小
         */
        private int connectionPoolSize;

        /**
         * 连接空闲超时，单位：毫秒
         */
        private int idleConnectionTimeout;

        /**
         * 命令等待超时，单位：毫秒
         */
        private int timeout;

        /**
         * 发布和订阅连接池大小
         */
        private int subscriptionConnectionPoolSize;

    }

    @Data
    @NoArgsConstructor
    public static class ClusterServersConfig {

        /**
         * 客户端名称
         */
        private String clientName;

        /**
         * master 最小空闲连接数
         */
        private int masterConnectionMinimumIdleSize;

        /**
         * master 连接池大小
         */
        private int masterConnectionPoolSize;

        /**
         * slave 最小空闲连接数
         */
        private int slaveConnectionMinimumIdleSize;

        /**
         * slave 连接池大小
         */
        private int slaveConnectionPoolSize;

        /**
         * 连接空闲超时，单位：毫秒
         */
        private int idleConnectionTimeout;

        /**
         * 命令等待超时，单位：毫秒
         */
        private int timeout;

        /**
         * 发布和订阅连接池大小
         */
        private int subscriptionConnectionPoolSize;

        /**
         * 读取模式
         */
        private ReadMode readMode;

        /**
         * 订阅模式
         */
        private SubscriptionMode subscriptionMode;

    }

}
