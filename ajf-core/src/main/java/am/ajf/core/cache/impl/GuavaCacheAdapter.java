package am.ajf.core.cache.impl;

import java.util.Set;

import am.ajf.core.cache.CacheAdapter;

import com.google.common.cache.Cache;

public class GuavaCacheAdapter implements CacheAdapter {
	
	private final String cacheName;
	private final Cache<Object, Object> cache;

	public GuavaCacheAdapter(String cacheName, Cache<Object, Object> cache) {
		super();
		this.cacheName = cacheName;
		this.cache = cache;
	}

	@Override
	public boolean exist(Object key) {
		return (null != get(key));
	}

	@Override
	public Object get(Object key) {
		Object value = cache.getIfPresent(key);
		return value;
	}

	@Override
	public void put(Object key, Object value) {
		cache.put(key, value);
	}

	@Override
	public Set<Object> keys() {
		return cache.asMap().keySet();
	}

	@Override
	public Object remove(Object key) {
		Object value = cache.getIfPresent(key);
		cache.invalidate(key);
		return value;
	}

	@Override
	public void clear() {
		cache.invalidateAll();
	}

	@Override
	public Object getDelegate() {
		return cache;
	}
	
	@Override
	public String getName() {
		return cacheName;
	}

}
