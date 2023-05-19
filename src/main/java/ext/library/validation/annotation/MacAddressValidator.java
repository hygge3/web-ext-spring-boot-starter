package ext.library.validation.annotation;

import cn.hutool.core.lang.Validator;
import ext.library.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * MAC 地址校验器
 */
public class MacAddressValidator implements ConstraintValidator<MacAddress, String> {

    private boolean notNull;

    @Override
    public void initialize(MacAddress constraintAnnotation) {
        this.notNull = constraintAnnotation.notNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isNotBlank(value)) {
            return Validator.isMac(value);
        }

        return !notNull;
    }

}