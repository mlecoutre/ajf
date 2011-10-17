package ajf.cache;

import ajf.cache.impl.InfinispanEmbeddedCacheManagerImpl;

public abstract class CacheFactory {

	private final static CacheManager cacheManager = new InfinispanEmbeddedCacheManagerImpl();
	
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
