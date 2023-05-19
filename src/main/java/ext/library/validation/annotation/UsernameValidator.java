package ext.library.validation.annotation;

import cn.hutool.core.lang.Validator;
import ext.library.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * 用户名校验器
 */
public class UsernameValidator implements ConstraintValidator<Username, String> {

    private boolean notNull;

    @Override
    public void initialize(Username constraintAnnotation) {
        this.notNull = constraintAnnotation.notNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isNotBlank(value)) {
            if (value.length() < 5) {
                return false;
            }
            if (value.contains("@")) {
                return false;
            }

            return !Validator.isMobile(value);
        }

        return !notNull;
    }

}