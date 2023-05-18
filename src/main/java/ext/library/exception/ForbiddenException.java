package ext.library.exception;

import lombok.NoArgsConstructor;

/**
 * 无权限异常
 */
@NoArgsConstructor
public class ForbiddenException extends RuntimeException {

    static final long serialVersionUID = -477721736529522496L;

    public ForbiddenException(String message) {
        super(message);
    }

}