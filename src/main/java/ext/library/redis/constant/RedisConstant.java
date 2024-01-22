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

    /**
     * Redis 分割 Key 拼接，参考 {@linkplain String#join(CharSequence, CharSequence...)}
     *
     * <blockquote>示例：
     * <pre>
     * {@code
     *     String message = Redis.join("Java", "is", "cool");
     *     // message returned is: "Java:is:cool"
     * }
     * </pre>
     * </blockquote>
     * <p>
     * 注意，如果元素为 null，则添加 {@code "null"}。
     *
     * @param elements 要连接在一起的元素。
     * @return 由 Redis Key 分隔符分隔的元素组成的新字符串
     */
    public static String separatorJoin(String... elements) {
        return String.join(RedisConstant.KEY_SEPARATOR, elements);
    }
}
