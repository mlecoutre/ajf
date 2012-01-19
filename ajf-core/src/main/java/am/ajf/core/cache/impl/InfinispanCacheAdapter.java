package am.ajf.core.cache.impl;

import java.util.Collection;

import am.ajf.core.cache.CacheAdapter;

public class InfinispanCacheAdapter implements CacheAdapter {
	
	private final org.infinispan.Cache<Object, Object> cache; 

	public InfinispanCacheAdapter(org.infinispan.Cache<Object, Object> cache) {
		super();
		this.cache = cache;
	}

	@Override
	public boolean exist(Object key) {
		return cache.containsKey(key);
	}

	@Override
	public Object get(Object key) {
		return cache.get(key);
	}

	@Override
	public void put(Object key, Object value) {
		cache.put(key, value);		
	}

	@Override
	public Collection<Object> keys() {
		return cache.keySet();
	}

	@Override
	public Object remove(Object key) {
		return cache.remove(key);		
	}

	@Override
	public void clear() {
		cache.clear();		
	}

	@Override
	public Object getDelegate() {
		return cache;
	}

	@Override
	public String getName() {
		return cache.getName();
	}	

}
