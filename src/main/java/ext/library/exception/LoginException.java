package ext.library.exception;

import lombok.NoArgsConstructor;

/**
 * 登录异常
 */
@NoArgsConstructor
public class LoginException extends RuntimeException {

    public LoginException(String message) {
        super(message);
    }

}