package ext.library.validation.annotation;

import cn.hutool.core.lang.Validator;
import ext.library.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * 邮政编码（中国）校验器
 */
public class ZipCodeValidator implements ConstraintValidator<ZipCode, String> {

    private boolean notNull;

    @Override
    public void initialize(ZipCode constraintAnnotation) {
        this.notNull = constraintAnnotation.notNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isNotBlank(value)) {
            return Validator.isZipCode(value);
        }

        return !notNull;
    }

}