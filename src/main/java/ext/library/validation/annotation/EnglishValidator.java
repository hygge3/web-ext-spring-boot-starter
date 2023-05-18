package ext.library.validation.annotation;

import ai.yue.library.base.util.StringUtils;
import cn.hutool.core.lang.Validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author	ylyue
 * @since	2019年5月8日
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
		
		if (notNull) {
			return false;
		}
		
		return true;
	}
	
}