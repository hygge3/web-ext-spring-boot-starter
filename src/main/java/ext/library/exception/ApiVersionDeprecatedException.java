package ext.library.exception;

import lombok.NoArgsConstructor;

/**
 * RESTful API 接口版本弃用异常
 */
@NoArgsConstructor
public class ApiVersionDeprecatedException extends RuntimeException {

    public ApiVersionDeprecatedException(String message) {
        super(message);
    }

}