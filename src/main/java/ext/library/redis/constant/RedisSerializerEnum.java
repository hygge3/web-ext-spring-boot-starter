package ext.library.redis.constant;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.Serializable;

/**
 * Redis 存储对象序列/反序列化
 */
public enum RedisSerializerEnum {

    /**
     * 序列化任何 {@link Serializable} 对象
     */
    JDK {
        @Override
        public RedisSerializer<Object> getRedisSerializer() {
            return new JdkSerializationRedisSerializer();
        }
    }, JACKSON {
        @Override
        public RedisSerializer<Object> getRedisSerializer() {
            return new GenericJackson2JsonRedisSerializer();
        }
    };

    public abstract RedisSerializer<Object> getRedisSerializer();

}
