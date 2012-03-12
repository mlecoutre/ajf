package am.ajf.core.cache;

import java.util.Set;


public interface CacheManager {
	
	/**
	 * @return the CacheManager provider name
	 */
	String getName();
	
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
	 * @return the set of caches name
	 */
	Set<String> cacheNames();
	
	/**
	 * clear all managed caches
	 */
	void clearAll();
	
}
