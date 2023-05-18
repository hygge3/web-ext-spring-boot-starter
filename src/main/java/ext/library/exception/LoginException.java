package ext.library.exception;

import lombok.NoArgsConstructor;

/**
 * 登录异常
 */
@NoArgsConstructor
public class LoginException extends RuntimeException {

    static final long serialVersionUID = -4747910085674257587L;

    public LoginException(String message) {
        super(message);
    }

}