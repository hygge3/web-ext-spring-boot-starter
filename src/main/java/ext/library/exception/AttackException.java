package ext.library.exception;

import lombok.NoArgsConstructor;

/**
 * 非法访问异常
 */
@NoArgsConstructor
public class AttackException extends RuntimeException {

    public AttackException(String message) {
        super(message);
    }

}