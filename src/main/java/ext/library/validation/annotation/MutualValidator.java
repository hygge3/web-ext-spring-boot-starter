package ext.library.validation.annotation;

import cn.hutool.core.util.ReflectUtil;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * 相互关系校验器
 */
public class MutualValidator implements ConstraintValidator<Mutual, Object> {

    private String[] mutuals;

    @Override
    public void initialize(Mutual constraintAnnotation) {
        this.mutuals = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // 相互关系逻辑，多个字段必须有一个有值
        boolean isMutualValueExist = false;
        for (String mutual : mutuals) {
            if (ReflectUtil.getFieldValue(value, mutual) != null) {
                isMutualValueExist = true;
            }
        }
        // 不满足相互关系
        return isMutualValueExist;
    }

}