package am.ajf.injection;

import java.lang.reflect.Method;

import am.ajf.core.cache.Cache;

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
