package ajf.cache.impl;

import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;

public class InfinispanCacheImpl implements ajf.cache.TTLCache {

	private transient Cache<Object, Object> cache = null;
	
	private long ttlInMs = -1;
	
	public InfinispanCacheImpl(Cache<Object, Object> cache) {
		super();
		this.cache = cache;
	}
	
	/**
	 * 
	 * @return the cache delegate
	 */
	public Cache<?, ?> getDelegate() {
		return this.cache;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.cache.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		InfinispanCacheImpl other = (InfinispanCacheImpl) obj;
		if (cache == null) {
			if (other.cache != null) return false;
		}
		else
			if (!cache.equals(other.cache)) return false;
		return true;
	}
	
	
	
}