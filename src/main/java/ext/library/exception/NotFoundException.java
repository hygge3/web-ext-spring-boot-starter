package ext.library.exception;

/**
 * 未找到异常
 *
 */
public class NotFoundException extends RuntimeException{
    public NotFoundException(String message) {
        super(message);
    }

}
