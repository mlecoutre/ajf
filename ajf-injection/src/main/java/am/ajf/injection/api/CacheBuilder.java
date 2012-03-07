package am.ajf.injection.api;

import java.lang.reflect.Method;

import am.ajf.core.cache.Cache;
import am.ajf.injection.annotation.Cached;

public interface CacheBuilder {

	/**
	 * 
	 * @param targetClass
	 * @param targetMethod
	 * @param cacheAnnotation
	 * @return Cache instance
	 */
	Cache build(Class<?> targetClass, Method targetMethod, Cached cacheAnnotation);
	
}
