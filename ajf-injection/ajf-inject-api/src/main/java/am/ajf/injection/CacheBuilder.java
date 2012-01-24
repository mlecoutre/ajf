package am.ajf.injection;

import am.ajf.core.cache.Cache;

public interface CacheBuilder {

	/**
	 * 
	 * @param targetClass
	 * @param cacheAnnotation
	 * @return Cache instance
	 */
	Cache build(Class<?> targetClass, Cached cacheAnnotation);
	
}
