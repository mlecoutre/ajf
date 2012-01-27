package am.ajf.core.cache;

import java.util.Collection;


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
	 * @return ajf named cache
	 */
	Cache getCache(String cacheName);
	
	/**
	 * 
	 * @param cacheName
	 * @param ttlInMs
	 * @return ajf named ttl cache
	 */
	Cache getCache(String cacheName, long ttlInMs);
	
	/**
	 * remove a cache from the CacheManager
	 * @param cacheName
	 */
	void removeCache(String cacheName);
	
	/**
	 * @return the collection of caches name
	 */
	Collection<String> caches();
	
	/**
	 * clear all managed caches
	 */
	void clearAll();
	
}
