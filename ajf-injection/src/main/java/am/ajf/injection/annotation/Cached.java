package am.ajf.injection.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

import am.ajf.injection.api.CacheBuilder;
import am.ajf.injection.api.KeyBuilder;
import am.ajf.injection.impl.DefaultCacheBuilder;
import am.ajf.injection.impl.SimpleKeyBuilderImpl;

@InterceptorBinding
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cached {
	
	@Nonbinding
	public String cacheManagerName() default "";

	@Nonbinding
	public String cacheName() default "";

	@Nonbinding
	public Class<? extends CacheBuilder> cacheBuilder() default DefaultCacheBuilder.class;
	
	@Nonbinding
	public boolean clearCache() default true;

	@Nonbinding
	public Class<? extends KeyBuilder> keyBuilder() default SimpleKeyBuilderImpl.class;
	
	@Nonbinding
	public String defaultKey() default "";
}
