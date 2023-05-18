package ext.library.exception;

import lombok.NoArgsConstructor;

/**
 * Admin 登录异常
 */
@NoArgsConstructor
public class AuthorizeException extends RuntimeException {

    static final long serialVersionUID = -4374582170487392015L;

    public AuthorizeException(String message) {
        super(message);
    }

}