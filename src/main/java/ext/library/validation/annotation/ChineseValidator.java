package ext.library.validation.annotation;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import ext.library.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * 汉字校验器
 */
public class ChineseValidator implements ConstraintValidator<Chinese, Object> {

    private boolean notNull;

    @Override
    public void initialize(Chinese constraintAnnotation) {
        this.notNull = constraintAnnotation.notNull();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        String validValue = null;
        if ((value != null && CharUtil.isChar(value) && !CharUtil.isBlankChar((char) value)) || (value instanceof String && StrUtil.isNotBlank((String) value))) {
            validValue = StrUtil.toString(value);
        }

        if (StringUtils.isNotBlank(validValue)) {
            return Validator.isChinese(validValue);
        }

        return !notNull;
    }

}