package am.ajf.injection;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import am.ajf.core.cache.Cache;
import am.ajf.core.cache.CacheManager;
import am.ajf.core.cache.CacheManagerFactory;

import com.google.common.base.Strings;

public class CacheProducer {

	public CacheProducer() {
		super();
	}

	@Produces
	public Cache produceCache(InjectionPoint ip) {
		
		CacheManager cacheManager = null;
		Cache cache = null;		
		
		if (ip.getAnnotated().isAnnotationPresent(am.ajf.injection.annotation.Cache.class)) {
			
			am.ajf.injection.annotation.Cache cacheAnnotation = ip.getAnnotated().getAnnotation(
					am.ajf.injection.annotation.Cache.class);
			
			String cacheProvider = cacheAnnotation.cacheProvider();
			if (Strings.isNullOrEmpty(cacheProvider)) {
				cacheManager = CacheManagerFactory.getFirstCacheManager();
			}
			else {
				cacheManager = CacheManagerFactory.getCacheManager(cacheProvider);
			}
			
			String cacheName = cacheAnnotation.cacheName();
			if (Strings.isNullOrEmpty(cacheName)) {
				cache = cacheManager.getCache();
			}
			else {
				cache = cacheManager.getCache(cacheName);
			}
			
		}
		else {
		
			cacheManager = CacheManagerFactory.getFirstCacheManager();
			cache = cacheManager.getCache();
				
		}
		
		return cache;
		
	}
	
}
