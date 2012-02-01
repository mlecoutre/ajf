package am.ajf.remoting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME) 
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Remote {
	String jndi() default "";
	Jndi[] value() default {};
	
	public @interface Jndi {
		String name();
		String jndi();
	}
	
}

