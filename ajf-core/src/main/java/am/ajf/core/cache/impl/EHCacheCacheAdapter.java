package am.ajf.core.cache.impl;

import java.util.Collection;

import net.sf.ehcache.Element;
import am.ajf.core.cache.CacheAdapter;

public class EHCacheCacheAdapter implements CacheAdapter {
	
	private final net.sf.ehcache.Cache cache; 

	public EHCacheCacheAdapter(net.sf.ehcache.Cache cache) {
		super();
		this.cache = cache;
	}

	@Override
	public boolean exist(Object key) {
		return cache.isKeyInCache(key);
	}

	@Override
	public Object get(Object key) {
		Element element = cache.get(key);
		if (null == element)
			return null;
		return element.getObjectValue();
	}

	@Override
	public void put(Object key, Object value) {
		Element element = buildElement(key, value);
		cache.put(element); 		
	}

	/**
	 * create a cacheable element
	 * @param key
	 * @param value
	 * @return
	 */
	protected Element buildElement(Object key, Object value) {
		Element element = new Element(key, value);
		return element;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<Object> keys() {
		return cache.getKeysWithExpiryCheck();
	}

	@Override
	public Object remove(Object key) {
		return cache.removeAndReturnElement(key);		
	}

	@Override
	public void clear() {
		cache.removeAll();
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
