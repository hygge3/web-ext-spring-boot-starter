package ext.library.redis.constant;

import com.alibaba.fastjson2.support.spring6.data.redis.GenericFastJsonRedisSerializer;
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
    }, FASTJSON {
        @Override
        public RedisSerializer<Object> getRedisSerializer() {
            return new GenericFastJsonRedisSerializer();
        }
    }, FASTJSONB {
        @Override
        public RedisSerializer<Object> getRedisSerializer() {
            return new GenericFastJsonRedisSerializer(true);
        }
    }, JACKSON {
        @Override
        public RedisSerializer<Object> getRedisSerializer() {
            return new GenericJackson2JsonRedisSerializer();
        }
    };

    public abstract RedisSerializer<Object> getRedisSerializer();

}