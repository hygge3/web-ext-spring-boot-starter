package ext.library.limiter;

/**
 * 限流异常
 */
public class RateLimitException extends RuntimeException {
    public RateLimitException(String msg) {
        super(msg);
    }
}