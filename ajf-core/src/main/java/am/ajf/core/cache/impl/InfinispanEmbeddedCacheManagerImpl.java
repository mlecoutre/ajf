package am.ajf.core.cache.impl;

import org.infinispan.config.Configuration;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import am.ajf.core.Service;
import am.ajf.core.cache.CacheManagerAdapter;

@SuppressWarnings("deprecation")
public class InfinispanEmbeddedCacheManagerImpl extends AbstractCacheManager
		implements CacheManagerAdapter, Service {

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
		clearAll();
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
	public String getName() {
		return INFINISPAN;
	}

	@Override
	public void addNativeCache(String cacheName, Object nativeCacheConfiguration) {

		if (!(nativeCacheConfiguration instanceof Configuration)) {
			throw new ClassCastException("Parameter must be instanceof '"
					.concat(Configuration.class.getName()).concat("'."));
		}

		Configuration cacheConfig = (Configuration) nativeCacheConfiguration;
		cacheManagerDelegate.defineConfiguration(cacheName, cacheConfig);

		am.ajf.core.cache.Cache adapterCache = new InfinispanCacheAdapter(
				cacheManagerDelegate.getCache(cacheName));
		cachesMap.put(cacheName, adapterCache);

	}

}
