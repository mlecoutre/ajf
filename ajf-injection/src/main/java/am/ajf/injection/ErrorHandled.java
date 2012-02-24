package am.ajf.injection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

/**
 * This annotation will be injected automatically by the ServicesExtension. It
 * will be used to decore BackingBeans, Policies and services to manage specific
 * error handling regarding the context.
 * 
 * It's use by the @Interceptor am.ajf.injection.ErrorHandlingInterceptor
 * 
 * @author E010925
 * 
 */
@InterceptorBinding
@Target({ ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorHandled {

}
