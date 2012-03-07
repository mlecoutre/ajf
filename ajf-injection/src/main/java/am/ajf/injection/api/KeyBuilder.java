package am.ajf.injection.api;

import java.lang.reflect.Method;

import am.ajf.injection.annotation.Cached;

public interface KeyBuilder {

	/**
	 * 
	 * @param cachedAnnotation
	 * @param parameters
	 * @return a key as cache entry
	 */
	Object build(Class<?> targetClass, Method targetMethod, Cached cachedAnnotation, Object[] parameters); 
	
}
