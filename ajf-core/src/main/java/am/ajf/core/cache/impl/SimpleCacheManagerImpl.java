package am.ajf.core.cache.impl;

import am.ajf.core.Service;
import am.ajf.core.cache.Cache;

public class SimpleCacheManagerImpl extends AbstractCacheManager
	implements Service {

	private static final String THREADLOCAL = "simple";

	private static final String DEFAULT_CACHE_NAME = "___defaultcache";
	

	public SimpleCacheManagerImpl() {
		super();
	}

	@Override
	public void start() {
		// Nothing to do
	}

	@Override
	public void stop() {
		clearAll();		
	}

	@Override
	public String getName() {
		return THREADLOCAL;
	}
	
	@Override
	protected Cache createDefaultCache() {
		return createCache(DEFAULT_CACHE_NAME);
	}

	@Override
	protected Cache createCache(String cacheName) {
		Cache cache = new SimpleCacheAdapter(cacheName);
		return cache;
	}

	@Override
	protected Cache createCache(String cacheName, long ttlInMs) {
		Cache cache = new SimpleCacheTtlAdapter(cacheName, ttlInMs);
		return cache;
	}

}
