package ext.library.validation.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 验证是否为车架号（别名：行驶证编号、车辆识别代号、车辆识别码）
 * <p>有效值必须是17位车架号；形如：LSJA24U62JG269225、LDC613P23A1305189</p>
 *
 * @author	ylyue
 * @since	2019年5月8日
 */
@Documented
@Retention(RUNTIME)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Constraint(validatedBy = { CarVinValidator.class })
@Repeatable(CarVin.List.class)
public @interface CarVin {
	
	/**
	 * 是否不允许为空 {@linkplain NotNull}
	 * @return 默认：true
	 */
	boolean notNull() default true;
	
	String message() default "{ai.yue.library.base.validation.annotation.CarVin.message}";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};

	/**
	 * Defines several {@code @CarVin} annotations on the same element.
	 */
	@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
	@Retention(RUNTIME)
	@Documented
	public @interface List {
		CarVin[] value();
	}

}