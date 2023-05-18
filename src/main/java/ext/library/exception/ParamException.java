package ext.library.exception;

/**
 * 参数校验不通过异常
 */
public class ParamException extends RuntimeException {

    static final long serialVersionUID = -7818277682527873103L;

    public ParamException(String msg) {
        super(msg);
    }

}