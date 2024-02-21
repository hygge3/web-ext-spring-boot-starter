package ext.library.redis.client;

import ext.library.util.SpringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 延迟队列
 *
 * @author zlh
 * @since 2024/02/20
 */
@Slf4j
@Data
@AllArgsConstructor
public class DelayQueue {
    private static final Long DELETE_SUCCESS = 1L;

    private static final StringRedisTemplate STRING_REDIS_TEMPLATE = SpringUtils.getBean(StringRedisTemplate.class);

    /**
     * 推
     *
     * @param key       钥匙
     * @param value     价值
     * @param timeUnit  时间单位
     * @param delayTime 延迟时间
     */
    public static void push(String key, String value, long delayTime, TimeUnit timeUnit) {
        // 保存数据至缓存
        STRING_REDIS_TEMPLATE.opsForZSet().add(key, value, System.currentTimeMillis() + timeUnit.toMillis(delayTime));
    }

    /**
     * 删除
     *
     * @param key   钥匙
     * @param value 价值
     */
    public static void delete(String key, String value) {
        STRING_REDIS_TEMPLATE.opsForZSet().remove(key, value);
    }

    /**
     * 弹出一个
     *
     * @param key 钥匙
     * @return {@link String}
     */
    public static synchronized String popOne(String key) {
        Set<String> sets = STRING_REDIS_TEMPLATE.opsForZSet().rangeByScore(key, 0, System.currentTimeMillis(), 0, 1);
        if (CollectionUtils.isEmpty(sets)) {
            return null;
        }

        for (String val : sets) {
            if (Objects.equals(STRING_REDIS_TEMPLATE.opsForZSet().remove(key, val), DELETE_SUCCESS)) {
                // 删除成功，表示抢占到
                return val;
            }
        }
        return null;
    }
}
