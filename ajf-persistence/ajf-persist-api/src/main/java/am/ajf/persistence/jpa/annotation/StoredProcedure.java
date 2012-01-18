package am.ajf.persistence.jpa.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME) 
@Target({ElementType.METHOD})
public @interface StoredProcedure {	
	String name();
	Class<?> resultClass();
}