package am.ajf.core.cache.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import am.ajf.core.cache.CacheAdapter;

public class ThreadLocalCacheTtlAdapter implements CacheAdapter {
	
	private final String cacheName;
	private final long ttlInMs;
	
	private final ThreadLocal<Map<Object, CachedObject>> cacheFactory;

	public ThreadLocalCacheTtlAdapter(String cacheName, long ttlInMs) {
		super();
		this.cacheName = cacheName;
		this.ttlInMs = ttlInMs;
		this.cacheFactory = new ThreadLocal<Map<Object,CachedObject>>();
	}
	
	public long getTtlInMs() {
		return ttlInMs;
	}

	private Map<Object, CachedObject> getCache() {
		Map<Object, CachedObject> cacheMap = cacheFactory.get();
		if (null == cacheMap) {
			cacheMap = initCache();
		}
		return cacheMap;
	}

	private synchronized  Map<Object, CachedObject> initCache() {
		Map<Object, CachedObject> cacheMap = cacheFactory.get();
		if (null != cacheMap)
			return cacheMap;
		
		cacheMap = new ConcurrentHashMap<Object, CachedObject>();
		cacheFactory.set(cacheMap);
		return cacheMap;
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
	public Set<Object> keys() {
		Set<Object> keys = new HashSet<Object>();
		 
		Map<Object, CachedObject> cache = getCache(); 
		long now = System.currentTimeMillis();
		
		for (Entry<Object, CachedObject> entry: cache.entrySet()) {
			Object key = entry.getKey();
			CachedObject cachedObject = entry.getValue();
		
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
