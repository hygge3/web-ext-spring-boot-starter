package ext.library.exception;

/**
 * 参数校验不通过异常
 */
public class ParamException extends RuntimeException {

    public ParamException(String msg) {
        super(msg);
    }

}