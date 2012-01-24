package am.ajf.core.cache.impl;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;

import am.ajf.core.cache.Cache;
import am.ajf.core.cache.CacheManagerAdapter;

public class GuavaCacheManagerImpl extends AbstractCacheManager implements
		CacheManagerAdapter {

	private static final String GUAVA = "guava";

	private static final String DEFAULT_CACHE_NAME = "___defaultcache";

	CacheBuilder<Object, Object> cacheBuilder;

	public GuavaCacheManagerImpl() {
		super();
	}

	@Override
	public void start() {
		cacheBuilder = CacheBuilder.newBuilder();
	}

	@Override
	public void stop() {
		cleanAll();		
	}
	
	@Override
	public String getProviderName() {
		return GUAVA;
	}

	@Override
	public Object getDelegate() {
		return cacheBuilder;
	}

	@Override
	protected Cache createDefaultCache() {
		return createCache(DEFAULT_CACHE_NAME);
	}

	@Override
	protected Cache createCache(String cacheName) {
		GuavaCacheAdapter guavaCacheAdapter = new GuavaCacheAdapter(DEFAULT_CACHE_NAME, cacheBuilder.build());
		return guavaCacheAdapter;
	}

	@Override
	protected Cache createCache(String cacheName, long ttlInMs) {
		com.google.common.cache.Cache<Object, Object> delegateCache = cacheBuilder
				.expireAfterAccess(ttlInMs, TimeUnit.MILLISECONDS).build();
		GuavaCacheAdapter guavaCacheAdapter = new GuavaCacheAdapter(cacheName, delegateCache);
		return guavaCacheAdapter;
	}

}
