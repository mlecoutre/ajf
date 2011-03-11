package ajf.cache;

public interface CacheManager {

	Cache getCache();
	
	Cache getCache(String cacheName);
	
}
