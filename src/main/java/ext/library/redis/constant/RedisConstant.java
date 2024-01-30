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
    public static final String KEY_PREFIX = Constant.CONFIG_PREFIX + KEY_SEPARATOR;

    /**
     * Redis 锁的 Key 前缀
     */
    public static final String LOCK_KEY_PREFIX = RedisConstant.standardKey("lock:");

    /**
     * Redis Token 的 Key 前缀
     */
    public static final String AUTH_KEY_PREFIX = RedisConstant.standardKey("auth:");

    /**
     * Redis 幂等性的 Key 前缀
     */
    public static final String API_IDEMPOTENT_KEY_PREFIX = RedisConstant.standardKey("api_idempotent:");

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
