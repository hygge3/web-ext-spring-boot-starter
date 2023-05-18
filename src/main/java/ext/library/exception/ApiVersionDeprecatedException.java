package ext.library.exception;

import lombok.NoArgsConstructor;

/**
 * RESTful API 接口版本弃用异常
 */
@NoArgsConstructor
public class ApiVersionDeprecatedException extends RuntimeException {

    static final long serialVersionUID = -8929648099790728526L;

    public ApiVersionDeprecatedException(String message) {
        super(message);
    }

}