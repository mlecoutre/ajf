package am.ajf.core.cache.impl;

import org.infinispan.config.Configuration;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import am.ajf.core.cache.CacheManagerAdapter;

public class InfinispanEmbeddedCacheManagerImpl extends AbstractCacheManager
		implements CacheManagerAdapter {

	private static final String INFINISPAN = "infinispan";
	
	EmbeddedCacheManager cacheManagerDelegate = null;

	public InfinispanEmbeddedCacheManagerImpl() {
		super();
	}

	@Override
	public Object getDelegate() {
		return cacheManagerDelegate;
	}

	@Override
	public void start() {
		cacheManagerDelegate = new DefaultCacheManager();
		cacheManagerDelegate.start();

		defaultCache = createDefaultCache();

	}

	@Override
	public void stop() {
		cacheManagerDelegate.stop();
		cleanAll();		
	}

	@Override
	protected am.ajf.core.cache.Cache createDefaultCache() {
		am.ajf.core.cache.Cache cache = new InfinispanCacheAdapter(
				cacheManagerDelegate.getCache());
		return cache;
	}

	@Override
	protected am.ajf.core.cache.Cache createCache(String cacheName) {
		am.ajf.core.cache.Cache cache = new InfinispanCacheAdapter(
				cacheManagerDelegate.getCache(cacheName));
		return cache;
	}

	@Override
	protected am.ajf.core.cache.Cache createCache(String cacheName, long ttlInMs) {
		Configuration config = new Configuration();
		config.setExpirationLifespan(ttlInMs);

		cacheManagerDelegate.defineConfiguration(cacheName, config);
		am.ajf.core.cache.Cache cache = new InfinispanCacheAdapter(
				cacheManagerDelegate.getCache(cacheName));
		return cache;
	}

	@Override
	public String getProviderName() {
		return INFINISPAN;
	}

}
