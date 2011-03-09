package ajf.cache.impl;

import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class InfinispanEmbeddedCacheImpl implements ajf.cache.TTLCache {

	private transient EmbeddedCacheManager cacheManager = null;
	private transient Cache<Object, Object> cache = null;
	
	private long ttlInMs = 0;
	
	public InfinispanEmbeddedCacheImpl() {
		super();
		init();
	}
	
	/**
	 * @return the ttlInMs
	 */
	public long getTtlInMs() {
		return ttlInMs;
	}

	/**
	 * @param ttlInMs the ttlInMs to set
	 */
	public void setTtlInMs(long ttlInMs) {
		this.ttlInMs = ttlInMs;
	}

	/**
	 * 
	 * @return the cache manager delegate
	 */
	public EmbeddedCacheManager getCacheManagerDelegate() {
		return this.cacheManager;
	}

	/**
	 * 
	 * @return the cache delegate
	 */
	public Cache<Object, Object> getCacheDelegate() {
		return this.cache;
	}

	/**
	 * 
	 * @return the cache manager delegate
	 */
	public EmbeddedCacheManager getDelegate() {
		return this.cacheManager;
	}
	
	/**
	 * init method 
	 */
	private void init() {
		this.cacheManager = new DefaultCacheManager();
		this.cache = this.cacheManager.getCache();
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void put(Object key, Object value) {
		if (this.ttlInMs <= 0) {
			this.cache.put(key, value);
		} 
		else {
			this.cache.put(key, value, this.ttlInMs, TimeUnit.MILLISECONDS);
		}		
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public Object get(Object key) {
		return this.cache.get(key);
	}
	
	/**
	 * 
	 * @param key
	 */
	public void remove(Object key) {
		this.cache.remove(key);
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public boolean contains(Object key) {
		return this.cache.containsKey(key);
	}
	
	/**
	 * 
	 */
	public void clear() {
		this.cache.clear();
	}

	/**
	 * 
	 */
	public boolean isEmpty() {
		return this.cache.isEmpty();
	}
}
