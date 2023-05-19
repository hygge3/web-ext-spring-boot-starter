package ext.library.validation.annotation;

import cn.hutool.core.lang.Validator;
import ext.library.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * 身份证号码（18 位中国）校验器
 */
public class IdCardValidator implements ConstraintValidator<IdCard, String> {

    private boolean notNull;

    @Override
    public void initialize(IdCard constraintAnnotation) {
        this.notNull = constraintAnnotation.notNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isNotBlank(value)) {
            return Validator.isCitizenId(value);
        }

        return !notNull;
    }

}