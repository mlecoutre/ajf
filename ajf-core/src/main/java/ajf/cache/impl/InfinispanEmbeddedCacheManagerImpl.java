package ajf.cache.impl;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import ajf.cache.CacheManager;

public class InfinispanEmbeddedCacheManagerImpl implements CacheManager {

	private transient EmbeddedCacheManager cacheManager = null;

	public InfinispanEmbeddedCacheManagerImpl() {
		super();
		this.cacheManager = new DefaultCacheManager();
		this.cacheManager.start();
	}

	/**
	 * 
	 * @return the cache manager delegate
	 */
	public EmbeddedCacheManager getDelegate() {
		return this.cacheManager;
	}

	@Override
	public <K, V>ajf.cache.Cache<K, V> getCache() {
		Cache<K, V> cache = this.cacheManager.getCache();
		return new InfinispanCacheImpl<K, V>(cache);
	}

	@Override
	public <K, V>ajf.cache.Cache<K, V> getCache(String cacheName) {
		Cache<K, V> cache = this.cacheManager.getCache(cacheName);
		return new InfinispanCacheImpl<K, V>(cache);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		this.cacheManager.stop();
		super.finalize();
	}

}
