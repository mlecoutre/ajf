package am.ajf.injection;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target( {FIELD, PARAMETER} )
@Retention(RUNTIME)
public @interface Property {
	public String value() default "";
	public String defaultValue() default "";
}
