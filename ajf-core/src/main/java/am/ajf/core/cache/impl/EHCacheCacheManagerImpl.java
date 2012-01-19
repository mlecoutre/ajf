package am.ajf.core.cache.impl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.config.CacheConfiguration;
import am.ajf.core.cache.CacheManagerAdapter;

public class EHCacheCacheManagerImpl extends AbstractCacheManager implements
		CacheManagerAdapter {

	private static final String EHCACHE = "ehcache";

	private static final String DEFAULT_CACHE_NAME = "___defaultcache";

	net.sf.ehcache.CacheManager cacheManagerDelegate = null;

	public EHCacheCacheManagerImpl() {
		super();
	}

	@Override
	public void start() {

		cacheManagerDelegate = net.sf.ehcache.CacheManager.create();
		defaultCache = createDefaultCache();

	}

	@Override
	public void stop() {
		cacheManagerDelegate.shutdown();
	}

	@Override
	public Object getDelegate() {
		return cacheManagerDelegate;
	}

	@Override
	protected am.ajf.core.cache.Cache createDefaultCache() {
		return createCache(DEFAULT_CACHE_NAME);
	}

	@Override
	protected am.ajf.core.cache.Cache createCache(String cacheName) {

		if (!cacheManagerDelegate.cacheExists(cacheName)) {
			Cache cache = new Cache(new CacheConfiguration(cacheName, 0)
					.overflowToDisk(false).eternal(true));
			cacheManagerDelegate.addCache(cache);
		}
		
		am.ajf.core.cache.Cache cache = new EHCacheCacheAdapter(
				cacheManagerDelegate.getCache(cacheName));
		return cache;
	}

	@Override
	protected am.ajf.core.cache.Cache createCache(String cacheName, long ttlInMs) {

		if (!cacheManagerDelegate.cacheExists(cacheName)) {
			Cache cache = new Cache(new CacheConfiguration(cacheName, 0)
				.overflowToDisk(false).eternal(true));
			cacheManagerDelegate.addCache(cache);
		}

		am.ajf.core.cache.Cache cache = new EHCacheCacheTtlAdapter(
				cacheManagerDelegate.getCache(cacheName), ttlInMs);
		return cache;
	}

	@Override
	public String getProviderName() {
		return EHCACHE;
	}

}
