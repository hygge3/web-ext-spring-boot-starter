package ext.library.exception;

import lombok.NoArgsConstructor;

/**
 * 服务降级异常
 */
@NoArgsConstructor
public class ClientFallbackException extends RuntimeException {

    static final long serialVersionUID = -3620957053991110208L;

    public ClientFallbackException(String message) {
        super(message);
    }

}