package ajf.cache;

public interface CacheManager{

	<K, V> Cache<K, V> getCache();
	
	<K, V> Cache<K, V> getCache(String cacheName);
	
}
