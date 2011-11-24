package ajf.cache;

import java.util.Map;

import ajf.cache.impl.InfinispanEmbeddedCacheManagerImpl;

public class CacheFactory {

	private static CacheManager cacheManager = new InfinispanEmbeddedCacheManagerImpl();
	
	private CacheFactory() {
		super();
	}
	
	public static CacheManager getCacheManager() {
		return cacheManager;
	}

	public static void setCacheManager(CacheManager cacheManager) {
		CacheFactory.cacheManager = cacheManager;
	}

	public static <K, V>Map<K, V>getCache() {
		return cacheManager.getCache();
	}
	
	public static <K, V>Map<K, V>getCache(String cacheName) {
		return cacheManager.getCache(cacheName);
	}
	
	public static <K, V>Map<K, V>getCache(String cacheName, long ttlInMs) {
		return cacheManager.getCache(cacheName, ttlInMs);
	}
	
}
