package ext.library.validation.annotation;

import cn.hutool.core.lang.Validator;
import ext.library.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * 字母（包括大写和小写字母）校验器
 */
public class EnglishValidator implements ConstraintValidator<English, String> {

    private boolean notNull;

    @Override
    public void initialize(English constraintAnnotation) {
        this.notNull = constraintAnnotation.notNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isNotBlank(value)) {
            return Validator.isWord(value);
        }

        return !notNull;
    }

}