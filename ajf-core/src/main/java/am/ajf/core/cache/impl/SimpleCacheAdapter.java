package am.ajf.core.cache.impl;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import am.ajf.core.cache.CacheAdapter;

public class SimpleCacheAdapter implements CacheAdapter {
	
	private final String cacheName;
	private final Map<Object, Object> cache;

	public SimpleCacheAdapter(String cacheName) {
		super();
		this.cacheName = cacheName;
		this.cache = new ConcurrentHashMap<Object, Object>();
	}
	
	private Map<Object, Object> getCache() {
		return cache;
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
	public Set<Object> keys() {
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
