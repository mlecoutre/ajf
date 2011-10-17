package ajf.cache.impl;

import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;

public class InfinispanCacheImpl<K, V> implements ajf.cache.TTLCache<K, V> {

	private transient Cache<K, V> cache = null;
	
	private long ttlInMs = -1;
	
	public InfinispanCacheImpl(Cache<K, V> cache) {
		super();
		this.cache = cache;
	}
	
	/**
	 * 
	 * @return the cache delegate
	 */
	public Cache<K, V> getDelegate() {
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
	public void put(K key, V value) {
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
	public V get(K key) {
		return this.cache.get(key);
	}
	
	/**
	 * 
	 * @param key
	 */
	public V remove(K key) {
		return this.cache.remove(key);
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public boolean contains(K key) {
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
