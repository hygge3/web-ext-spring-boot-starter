package ext.library.validation.annotation;

import cn.hutool.core.lang.Validator;
import ext.library.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * UUID 校验器
 */
public class UUIDValidator implements ConstraintValidator<UUID, String> {

    private boolean notNull;

    @Override
    public void initialize(UUID constraintAnnotation) {
        this.notNull = constraintAnnotation.notNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isNotBlank(value)) {
            return Validator.isUUID(value);
        }

        return !notNull;
    }

}