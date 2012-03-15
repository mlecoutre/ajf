package am.ajf.injection.annotation;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import am.ajf.core.beans.BeansManager;

@Qualifier
@Retention(RUNTIME)
@Target({TYPE, FIELD, PARAMETER, CONSTRUCTOR})
public @interface Bean {
	
	public String value() default BeansManager.DEFAULT_BEAN_NAME;
	
}
