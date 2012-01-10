package ajf.persistence.jpa.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.util.Nonbinding;

@Documented
@Retention(RetentionPolicy.RUNTIME) 
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface PersistenceUnit {
	@Nonbinding String name() default "default";
}

