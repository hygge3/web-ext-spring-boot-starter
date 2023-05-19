package ext.library.validation.annotation;

import cn.hutool.core.lang.Validator;
import ext.library.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * IPV4 地址校验器
 */
public class IPV4Validator implements ConstraintValidator<IPV4, String> {

    private boolean notNull;

    @Override
    public void initialize(IPV4 constraintAnnotation) {
        this.notNull = constraintAnnotation.notNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isNotBlank(value)) {
            return Validator.isIpv4(value);
        }

        return !notNull;
    }

}