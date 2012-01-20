package am.ajf.core.cache;


public interface CacheManager{
	
	/**
	 * @return the CacheManager provider name
	 */
	String getProviderName();
	
	/**
	 * start the CacheManager
	 */
	void start();
	
	/**
	 * stop the CacheManager
	 */
	void stop();
	
	/**
	 * 
	 * @return ajf Cache
	 */	
	Cache getCache();
	
	/**
	 * 
	 * @param cacheName
	 * @return ajf nammed cache
	 */
	Cache getCache(String cacheName);
	
	/**
	 * 
	 * @param cacheName
	 * @param ttlInMs
	 * @return ajf nammed ttl cache
	 */
	Cache getCache(String cacheName, long ttlInMs);
	
}
