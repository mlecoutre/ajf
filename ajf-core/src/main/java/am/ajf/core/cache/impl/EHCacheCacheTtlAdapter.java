package am.ajf.core.cache.impl;

import net.sf.ehcache.Element;

public class EHCacheCacheTtlAdapter extends EHCacheCacheAdapter {
	
	private final int ttlInSec; 

	public EHCacheCacheTtlAdapter(net.sf.ehcache.Cache cache, long ttlInMs) {
		super(cache);
		this.ttlInSec = Math.round((float)(ttlInMs/1000));
	}

	@Override
	protected Element buildElement(Object key, Object value) {
		Element element = new Element(key, value);
		element.setTimeToLive(ttlInSec);
		return element;
	}

	public int getTtlInSec() {
		return ttlInSec;
	}
	
}
