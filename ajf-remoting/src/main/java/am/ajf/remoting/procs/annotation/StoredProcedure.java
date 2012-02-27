package am.ajf.remoting.procs.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import am.ajf.remoting.Mapper;
import am.ajf.remoting.SimpleFieldNamesMapper;

@Documented
@Retention(RetentionPolicy.RUNTIME) 
@Target({ElementType.METHOD})
public @interface StoredProcedure {	
	String name();
	Class<? extends Mapper> mapper() default SimpleFieldNamesMapper.class;
}
