package am.ajf.injection.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;

@Target(FIELD)
@Retention(RUNTIME)
public @interface Cache {
	
	@Nonbinding
	public String cacheProvider() default "";

	@Nonbinding
	public String cacheName() default "";
	
}
