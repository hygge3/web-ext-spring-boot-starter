package ext.library.redis.dto;

import ext.library.util.DateUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * Redis Lock
 */
@Data
public class LockInfo {

    /**
     * 是否成功拿到锁
     */
    boolean lock;

    /**
     * 分布式锁的 key（全局唯一性）
     */
    @NotBlank String lockKey;

    /**
     * 分布式锁的超时时间（单位：毫秒），到期后锁将自动超时
     */
    @NotBlank Integer lockTimeoutMs;

    /**
     * 分布式锁的超时时间戳（<code style="color:red">当前时间戳 + 超时毫秒</code>，即：{@link DateUtils#getTimestamp(int)} + {@link #lockTimeoutMs}）
     */
    @NotBlank String lockTimeoutStamp;

}