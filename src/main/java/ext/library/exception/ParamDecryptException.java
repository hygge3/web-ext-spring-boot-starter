package ext.library.exception;

import lombok.NoArgsConstructor;

/**
 * 解密异常
 */
@NoArgsConstructor
public class ParamDecryptException extends RuntimeException {

    static final long serialVersionUID = 5325379409661261173L;

    public ParamDecryptException(String message) {
        super(message);
    }

}