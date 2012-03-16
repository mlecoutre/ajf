package am.ajf.injection.impl;

import java.lang.reflect.Method;

import com.google.common.base.Strings;

import am.ajf.core.cache.Cache;
import am.ajf.core.cache.CacheManager;
import am.ajf.core.cache.CacheManagerFactory;
import am.ajf.injection.annotation.Cached;
import am.ajf.injection.api.CacheBuilder;

public class DefaultCacheBuilder implements CacheBuilder {

	public DefaultCacheBuilder() {
		super();
	}

	@Override
	public Cache build(Class<?> targetClass, Method targetMethod,
			Cached cacheAnnotation) {

		CacheManager cacheManager = CacheManagerFactory.getFirstCacheManager();

		String cacheProvider = cacheAnnotation.cacheManagerName();
		if (Strings.isNullOrEmpty(cacheProvider)) {
			cacheManager = CacheManagerFactory.getCacheManager(CacheManagerFactory.SIMPLE_CACHE_MANAGER);
		}
		else {
			cacheManager = CacheManagerFactory.getCacheManager(cacheProvider);
		}
		
		String cacheManagerName = cacheAnnotation.cacheManagerName();
		if (!Strings.isNullOrEmpty(cacheManagerName)) {
			cacheManager = CacheManagerFactory.getCacheManager(cacheManagerName);
		}

		String cacheName = cacheAnnotation.cacheName();
		if (Strings.isNullOrEmpty(cacheName))
			cacheName = targetClass.getName().concat("#")
					.concat(targetMethod.getName());

		Cache cache = cacheManager.getCache(cacheName);
		return cache;
	}

}
