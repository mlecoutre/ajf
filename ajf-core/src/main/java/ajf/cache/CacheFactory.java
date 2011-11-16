package ajf.cache;

import ajf.cache.impl.InfinispanEmbeddedCacheManagerImpl;

public class CacheFactory {

	private static CacheManager cacheManager = new InfinispanEmbeddedCacheManagerImpl();
	
	protected CacheFactory() {
		super();
	}
	
	protected static CacheManager getCacheManager() {
		return cacheManager;
	}

	protected static void setCacheManager(CacheManager cacheManager) {
		CacheFactory.cacheManager = cacheManager;
	}

	public static <K, V>Cache<K, V>getCache() {
		return cacheManager.getCache();
	}
	
	public static <K, V>Cache<K, V>getCache(String cacheName) {
		return cacheManager.getCache(cacheName);
	}
	
	public static <K, V>TTLCache<K, V>getTTLCache() {
		Cache<K, V> cache =  cacheManager.getCache();
		return (TTLCache<K, V>) cache;
	}
	
	public static <K, V>TTLCache<K, V>getTTLCache(String cacheName) {
		Cache<K, V> cache = cacheManager.getCache(cacheName);
		return (TTLCache<K, V>) cache;
	}
	
}
