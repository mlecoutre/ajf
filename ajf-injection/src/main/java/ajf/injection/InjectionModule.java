package ajf.injection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.Module;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface InjectionModule {

	/**
	 * @return a Runner class (must have a constructor that takes a single Class to run)
	 */
	Class<? extends Module> value();

}