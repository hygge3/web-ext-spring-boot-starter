package ext.library.validation.annotation;

import cn.hutool.core.lang.Validator;
import ext.library.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * 手机号码（中国）校验器
 */
public class CellphoneValidator implements ConstraintValidator<Cellphone, String> {

    private boolean notNull;

    @Override
    public void initialize(Cellphone constraintAnnotation) {
        this.notNull = constraintAnnotation.notNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isNotBlank(value)) {
            return Validator.isMobile(value);
        }

        return !notNull;
    }

}