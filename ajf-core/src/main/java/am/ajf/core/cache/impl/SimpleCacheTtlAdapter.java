package am.ajf.core.cache.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import am.ajf.core.cache.CacheAdapter;

public class SimpleCacheTtlAdapter implements CacheAdapter {
	
	private final String cacheName;
	private final long ttlInMs;
	
	private final Map<Object, CachedObject> cache;

	public SimpleCacheTtlAdapter(String cacheName, long ttlInMs) {
		super();
		this.cacheName = cacheName;
		this.ttlInMs = ttlInMs;
		this.cache = new ConcurrentHashMap<Object, CachedObject>();
	}

	private Map<Object, CachedObject> getCache() {
		return cache;
	}
	
	public long getTtlInMs() {
		return ttlInMs;
	}

	@Override
	public boolean exist(Object key) {
		
		Map<Object, CachedObject> cache = getCache(); 
		if (!cache.containsKey(key))
			return false;
		
		CachedObject cachedObject = cache.get(key);
		long now = System.currentTimeMillis();
		
		if ((cachedObject.getLastAccess() - now) <= ttlInMs) {
			cachedObject.updateLastAccess();
			return true;
		}
		else {
			cache.remove(key);
			return false;
		}		
		
	}
	
	@Override
	public Object get(Object key) {
		
		Map<Object, CachedObject> cache = getCache(); 
		if (!cache.containsKey(key))
			return null;
		
		CachedObject cachedObject = cache.get(key);
		long now = System.currentTimeMillis();
		
		if ((cachedObject.getLastAccess() - now) <= ttlInMs) {
			return cachedObject.getValue();
		}
		else {
			cache.remove(key);
			return null;
		}
		
	}

	@Override
	public void put(Object key, Object value) {
		CachedObject cachedObject = new CachedObject(value);
		getCache().put(key, cachedObject);
	}

	@Override
	public Collection<Object> keys() {
		Collection<Object> keys = new ArrayList<Object>();
		 
		Map<Object, CachedObject> cache = getCache(); 
		long now = System.currentTimeMillis();
		
		for (Object key: cache.keySet()) {
			CachedObject cachedObject = cache.get(key);
			if ((cachedObject.getLastAccess() - now) <= ttlInMs) {
				cachedObject.updateLastAccess();
				keys.add(key);
			}
			else {
				cache.remove(key);
			}			
		}
		
		return keys;
	}

	@Override
	public Object remove(Object key) {
		CachedObject cachedObject = getCache().remove(key);
		return cachedObject.getValue();
	}

	@Override
	public void clear() {
		getCache().clear();
	}

	@Override
	public Object getDelegate() {
		return getCache();
	}
	
	@Override
	public String getName() {
		return cacheName;
	}

}