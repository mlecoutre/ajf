package am.ajf.injection;

import java.lang.reflect.Method;

public interface KeyBuilder {

	/**
	 * 
	 * @param cachedAnnotation
	 * @param parameters
	 * @return a key as cache entry
	 */
	Object build(Class<?> targetClass, Method targetMethod, Cached cachedAnnotation, Object[] parameters); 
	
}
