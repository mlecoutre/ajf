package am.ajf.injection.annotation;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;

@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER, CONSTRUCTOR})
public @interface Cache {
	
	@Nonbinding
	public String cacheManagerName() default "";

	@Nonbinding
	public String cacheName() default "";
	
}
