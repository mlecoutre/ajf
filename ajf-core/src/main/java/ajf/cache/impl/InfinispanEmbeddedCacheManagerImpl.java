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
	public ajf.cache.Cache getCache() {
		Cache<Object, Object> cache = this.cacheManager.getCache();
		return new InfinispanCacheImpl(cache);
	}

	@Override
	public ajf.cache.Cache getCache(String cacheName) {
		Cache<Object, Object> cache = this.cacheManager.getCache(cacheName);
		return new InfinispanCacheImpl(cache);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		this.cacheManager.stop();
		super.finalize();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.cacheManager.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		InfinispanEmbeddedCacheManagerImpl other = (InfinispanEmbeddedCacheManagerImpl) obj;
		if (cacheManager == null) {
			if (other.cacheManager != null) return false;
		}
		else
			if (!cacheManager.equals(other.cacheManager)) return false;
		return true;
	}
	
	
	

}
