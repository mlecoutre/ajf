package am.ajf.core.cache.impl;

import java.util.concurrent.TimeUnit;

import am.ajf.core.Service;
import am.ajf.core.cache.Cache;
import am.ajf.core.cache.CacheManagerAdapter;

import com.google.common.cache.CacheBuilder;

public class GuavaCacheManagerImpl extends AbstractCacheManager 
	implements CacheManagerAdapter, Service {

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
		clearAll();		
	}
	
	@Override
	public String getName() {
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
	
	@Override
	public void addNativeCache(String cacheName, Object nativeCache) {
		if (!(nativeCache instanceof com.google.common.cache.Cache<?, ?>)) {
			throw new ClassCastException("Parameter must be instanceof '"
					.concat(com.google.common.cache.Cache.class.getName()).concat("'."));
		}

		@SuppressWarnings("unchecked")
		com.google.common.cache.Cache<Object, Object> cache = (com.google.common.cache.Cache<Object, Object>) nativeCache;
		
		am.ajf.core.cache.Cache adapterCache = new GuavaCacheAdapter(cacheName, cache);
		cachesMap.put(cacheName, adapterCache);

	}

}
