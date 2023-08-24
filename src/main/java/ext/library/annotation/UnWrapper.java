package ext.library.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 不包装
 * <p>返回原始值</p>
 */
@Target({METHOD, TYPE})
@Retention(RUNTIME)
@Documented
public @interface UnWrapper {}
