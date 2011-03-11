package ajf.cache.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import ajf.cache.Cache;
import ajf.cache.CacheManager;
import ajf.cache.TTLCache;
import ajf.cache.impl.InfinispanEmbeddedCacheManagerImpl;

public class CacheTest {

	@Test
	public void testGetCache() {
		
		CacheManager cacheManager = new InfinispanEmbeddedCacheManagerImpl();
		Cache cache = cacheManager.getCache();
		
		assertNotNull("Unable to obtain a cache instance", cache);		
	}
	
	@Test
	public void testGetNammedCache() {
		
		CacheManager cacheManager = new InfinispanEmbeddedCacheManagerImpl();
		Cache cache = cacheManager.getCache("myCache");
		
		assertNotNull("Unable to obtain a named cache instance", cache);		
	}
	
	@Test
	public void testGetObjectFromCache() {
		
		CacheManager cacheManager = new InfinispanEmbeddedCacheManagerImpl();
		Cache cache = cacheManager.getCache();
		
		String key = "key";
		// put in cache
		cache.put(key, "myValue");
		// get from cache
		String cachedValue = (String) cache.get(key);
		
		assertNotNull("Unable to get a cached object", cachedValue);		
	}
	
	@Test
	public void testGetObjectFromCacheWithTTL() throws InterruptedException {
		
		CacheManager cacheManager = new InfinispanEmbeddedCacheManagerImpl();
		TTLCache cache = (TTLCache) cacheManager.getCache();
		// set ttl of 2 seconds
		cache.setTtlInMs(2000);
		
		String key = "key";
		// put in cache
		cache.put(key, "myValue");
		// get from cache
		String cachedValue = (String) cache.get(key);
		assertNotNull("Unable to get a cached object", cachedValue);
		
		Thread.sleep(2500);
		
		cachedValue = (String) cache.get(key);
		assertNull("The cached object doesn't have to be available", cachedValue);
	}
	
}
