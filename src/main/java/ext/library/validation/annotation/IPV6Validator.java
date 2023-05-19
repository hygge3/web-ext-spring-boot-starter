package ext.library.validation.annotation;

import cn.hutool.core.lang.Validator;
import ext.library.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * IPV6 地址校验器
 */
public class IPV6Validator implements ConstraintValidator<IPV6, String> {

    private boolean notNull;

    @Override
    public void initialize(IPV6 constraintAnnotation) {
        this.notNull = constraintAnnotation.notNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isNotBlank(value)) {
            return Validator.isIpv6(value);
        }

        return !notNull;
    }

}