package ext.library.redis.constant;

import ext.library.constant.Constant;

/**
 * Redis 常量
 */
public class RedisConstant {

    /**
     * Redis Key 分隔符
     */
    public static final String KEY_SEPARATOR = ":";

    /**
     * Redis Key 前缀
     */
    public static final String KEY_PREFIX = Constant.PROJECT_PREFIX + KEY_SEPARATOR + "redis" + KEY_SEPARATOR;

    /**
     * Redis 锁的 Key 前缀
     */
    public static final String LOCK_KEY_PREFIX = RedisConstant.standardKey("lock:");

    /**
     * Redis 幂等性的 Key 前缀
     */
    public static final String API_IDEMPOTENT_KEY_PREFIX = RedisConstant.standardKey("api_idempotent:");

    /**
     * 幂等性版本号请求 key
     */
    public static final String API_IDEMPOTENT_VERSION_REQUEST_KEY = "apiIdempotentVersion";

    /**
     * 规范 Redis Key
     *
     * @param key Redis Key
     * @return 加上 yue 前缀的 key
     */
    public static String standardKey(String key) {
        return KEY_PREFIX + key;
    }

}