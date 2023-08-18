package ext.library.exception;

import lombok.NoArgsConstructor;

/**
 * 无权限异常
 */
@NoArgsConstructor
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

}