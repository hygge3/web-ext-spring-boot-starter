package ext.library.validation.annotation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 枚举值，必须是指定列表中其中个值
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Constraint(validatedBy = {EnumCodeValidator.class})
@Repeatable(EnumCode.List.class)
public @interface EnumCode {
    /**
     * 是否不允许为空 {@linkplain NotNull}
     *
     * @return 默认：true
     */
    boolean notNull() default true;
    /**
     * 枚举值，必须是指定列表中其中个值
     */
    int[] value() default {};

    String message() default "{ext.library.base.validation.annotation.Enum.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@code @EnumValue} annotations on the same element.
     */
    @Target({TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        EnumCode[] value();
    }
}
