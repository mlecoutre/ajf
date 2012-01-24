package am.ajf.core.cache.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import am.ajf.core.cache.CacheAdapter;

public class ThreadLocalCacheAdapter implements CacheAdapter {
	
	private final String cacheName;
	private final ThreadLocal<Map<Object, Object>> cacheFactory;

	public ThreadLocalCacheAdapter(String cacheName) {
		super();
		this.cacheName = cacheName;
		this.cacheFactory = new ThreadLocal<Map<Object,Object>>();
	}

	private Map<Object, Object> getCache() {
		Map<Object, Object> cacheMap = cacheFactory.get();
		if (null == cacheMap) {
			cacheMap = initCache();
		}
		return cacheMap;
	}

	private synchronized  Map<Object, Object> initCache() {
		Map<Object, Object> cacheMap = cacheFactory.get();
		if (null != cacheMap)
			return cacheMap;
		cacheMap = new ConcurrentHashMap<Object, Object>();
		cacheFactory.set(cacheMap);
		return cacheMap;
	}
	
	@Override
	public boolean exist(Object key) {
		return getCache().containsKey(key);
	}
	
	@Override
	public Object get(Object key) {
		Object value = getCache().get(key);
		return value;
	}

	@Override
	public void put(Object key, Object value) {
		getCache().put(key, value);
	}

	@Override
	public Collection<Object> keys() {
		return getCache().keySet();
	}

	@Override
	public Object remove(Object key) {
		Object value = getCache().remove(key);
		return value;
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
