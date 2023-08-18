package ext.library.exception;

import lombok.NoArgsConstructor;

/**
 * 服务降级异常
 */
@NoArgsConstructor
public class ClientFallbackException extends RuntimeException {

    public ClientFallbackException(String message) {
        super(message);
    }

}