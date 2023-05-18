package ext.library.validation.annotation;

import ai.yue.library.base.util.StringUtils;
import cn.hutool.core.lang.Validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author	ylyue
 * @since	2019年5月8日
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
		
		if (notNull) {
			return false;
		}
		
		return true;
	}
	
}