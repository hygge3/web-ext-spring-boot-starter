package ext.library.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 验证相互关系，多个字段必须有一个有值
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE})
@Constraint(validatedBy = {MutualValidator.class})
@Repeatable(Mutual.List.class)
public @interface Mutual {

    /**
     * 相互关系，多个字段必须有一个有值
     */
    String[] value() default {};

    String message() default "{ext.library.base.validation.annotation.Mutual.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@code @Mutual} annotations on the same element.
     */
    @Target({TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        Mutual[] value();
    }

}