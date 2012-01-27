package am.ajf.core.cache.impl;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import am.ajf.core.cache.Cache;
import am.ajf.core.cache.CacheManager;

public abstract class AbstractCacheManager implements CacheManager {

	protected Cache defaultCache;
	protected ConcurrentHashMap<String, Cache> cachesMap = new ConcurrentHashMap<String, Cache>();
	
	public AbstractCacheManager() {
		super();
	}

	@Override
	public Cache getCache() {
		if (null == defaultCache) {
			Cache cache = createDefaultCache();
			defaultCache = cache;
		}
		return defaultCache;
	}

	abstract protected Cache createDefaultCache();
	
	@Override
	public Cache getCache(String cacheName) {
		if (!cachesMap.containsKey(cacheName)) { 
			Cache cache = createCache(cacheName);
			cachesMap.put(cacheName, cache);
		}
		return cachesMap.get(cacheName);
	}

	abstract protected Cache createCache(String cacheName);

	@Override
	public Cache getCache(String cacheName, long ttlInMs) {
		if (!cachesMap.containsKey(cacheName)) { 
			Cache cache = createCache(cacheName, ttlInMs);
			cachesMap.put(cacheName, cache);
		}
		return cachesMap.get(cacheName);
	}
	
	@Override
	public void clearAll() {
		if (null != defaultCache)
			defaultCache.clear();
		for (Cache cache : cachesMap.values()) {
			cache.clear();
		}
	}
	
	@Override
	public void removeCache(String cacheName) {
		if (cachesMap.containsKey(cacheName)) { 
			Cache cache = cachesMap.get(cacheName);
			cache.clear();
			cachesMap.remove(cacheName);
		}		
	}

	@Override
	public Collection<String> caches() {
		Set<String> keys = cachesMap.keySet();
		return keys;
	}

	abstract protected Cache createCache(String cacheName, long ttlInMs);

}
