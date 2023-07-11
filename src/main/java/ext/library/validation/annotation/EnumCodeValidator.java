package ext.library.validation.annotation;

import ext.library.util.ObjectUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 枚举值校验器
 */
public class EnumCodeValidator implements ConstraintValidator<EnumCode, Integer> {

    private boolean notNull;
    private int[] enums;

    @Override
    public void initialize(EnumCode constraintAnnotation) {
        this.enums = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (ObjectUtils.isNull(value)) {
            return !notNull;
        }
        // 枚举值，必须是指定列表中其中个值
        for (int anEnum : enums) {
            if (value == anEnum) {
                return true;
            }
        }
        return false;

    }
}
