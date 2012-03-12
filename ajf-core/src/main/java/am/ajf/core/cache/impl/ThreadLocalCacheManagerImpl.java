package am.ajf.core.cache.impl;

import am.ajf.core.beans.LifecycleAware;
import am.ajf.core.cache.Cache;

public class ThreadLocalCacheManagerImpl extends AbstractCacheManager
	implements LifecycleAware {

	private static final String THREADLOCAL = "threadlocal";

	private static final String DEFAULT_CACHE_NAME = "___defaultcache";
	

	public ThreadLocalCacheManagerImpl() {
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
		Cache cache = new ThreadLocalCacheAdapter(cacheName);
		return cache;
	}

	@Override
	protected Cache createCache(String cacheName, long ttlInMs) {
		Cache cache = new ThreadLocalCacheTtlAdapter(cacheName, ttlInMs);
		return cache;
	}

}
