package ext.library.limiter;

/**
 * redis 限制异常
 */
public class RedisLimitException extends RuntimeException{
    public RedisLimitException(String msg) {
        super( msg );
    }
}
