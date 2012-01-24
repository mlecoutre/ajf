package am.ajf.injection.impl;

import com.google.common.base.Strings;

import am.ajf.core.cache.Cache;
import am.ajf.core.cache.CacheManager;
import am.ajf.core.cache.CacheManagerFactory;
import am.ajf.injection.CacheBuilder;
import am.ajf.injection.Cached;

public class DefaultCacheBuilder implements CacheBuilder {
		
	public DefaultCacheBuilder() {
		super();
	}

	@Override
	public Cache build(Class<?> targetClass, Cached cacheAnnotation) {
		
		CacheManager cacheManager = CacheManagerFactory.getFirstCacheManager();
		
		String cacheProvider = cacheAnnotation.cacheProvider();
		if (!Strings.isNullOrEmpty(cacheProvider)) {
			cacheManager = CacheManagerFactory.getCacheManager(cacheProvider);
		}
		
		String cacheName = cacheAnnotation.cacheName();
		if (Strings.isNullOrEmpty(cacheName))
			cacheName = targetClass.getName();
		
		Cache cache = cacheManager.getCache(cacheName);
		return cache;
	}

}
