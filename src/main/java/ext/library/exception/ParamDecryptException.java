package ext.library.exception;

import lombok.NoArgsConstructor;

/**
 * 解密异常
 */
@NoArgsConstructor
public class ParamDecryptException extends RuntimeException {

    public ParamDecryptException(String message) {
        super(message);
    }

}