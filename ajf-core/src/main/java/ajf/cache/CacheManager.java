package ajf.cache;

import java.util.Map;

public interface CacheManager{

	<K, V> Map<K, V> getCache();
	
	<K, V> Map<K, V> getCache(String cacheName);
	
	<K, V> Map<K, V> getCache(String cacheName, long ttlInMs);
	
}
