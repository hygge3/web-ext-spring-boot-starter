package ext.library.exception;

import lombok.NoArgsConstructor;

/**
 * 非法访问异常
 */
@NoArgsConstructor
public class AttackException extends RuntimeException {

    static final long serialVersionUID = 8503754532487989211L;

    public AttackException(String message) {
        super(message);
    }

}