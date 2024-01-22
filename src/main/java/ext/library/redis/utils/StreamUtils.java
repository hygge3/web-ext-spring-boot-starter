package ext.library.redis.utils;

import ext.library.util.SpringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;

/**
 * redis Stream 工具类
 *
 * @author Hygge
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StreamUtils {
    private static final StringRedisTemplate TEMPLATE = SpringUtils.getBean(StringRedisTemplate.class);

    /**
     * 创建消费者组
     *
     * @param streamName 流名称
     * @param groupName  组名称
     */
    public static void createGroup(final String streamName, final String groupName) {
        Boolean hasKey = TEMPLATE.hasKey(streamName);
        if (Boolean.TRUE.equals(hasKey)) {
            StreamInfo.XInfoGroups groups = TEMPLATE.opsForStream().groups(streamName);
            if (groups.isEmpty()) {
                TEMPLATE.opsForStream().createGroup(streamName, groupName);
            }
        } else {
            TEMPLATE.opsForStream().createGroup(streamName, groupName);
        }
    }

    /**
     * 销毁消费者组
     *
     * @param streamName 流名称
     * @return boolean
     */
    public static Boolean destroyGroup(final String streamName, final String groupName) {
        return TEMPLATE.opsForStream().destroyGroup(streamName, groupName);
    }

    public static <T> RecordId produce(final String streamName, final String key, final T value) {
        return TEMPLATE.opsForStream().add(streamName, Map.of(key, value));
    }

    public static <T> RecordId produce(final String streamName, final Map<String, T> content, final RecordId id) {
        return TEMPLATE.opsForStream().add(MapRecord.create(streamName, content).withId(id));
    }

    public static <T> RecordId produce(final String streamName, final Map<String, T> content) {
        return TEMPLATE.opsForStream().add(streamName, content);
    }

    public static <T> RecordId produceAndTrim(final String streamName, final String key, final T value, final long trimCount) {
        StreamOperations<String, Object, Object> stream = TEMPLATE.opsForStream();
        RecordId id = stream.add(streamName, Map.of(key, value));
        stream.trim(streamName, trimCount, true);
        return id;
    }

    public static <T> RecordId produceAndTrim(final String streamName, final Map<String, T> content, final long trimCount) {
        StreamOperations<String, Object, Object> stream = TEMPLATE.opsForStream();
        RecordId id = stream.add(streamName, content);
        stream.trim(streamName, trimCount, true);
        return id;
    }

    public static <T> void trim(final String streamName, final long trimCount) {
        StreamOperations<String, Object, Object> stream = TEMPLATE.opsForStream();
        stream.trim(streamName, trimCount, true);
    }

}
