package am.ajf.core.cache;


public interface CacheManager{
	
	void start();
	void stop();
	
	String getProviderName();
	
	Cache getCache();
	
	Cache getCache(String cacheName);
	
	Cache getCache(String cacheName, long ttlInMs);
	
}
