package ajf.cache;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class CacheTest {

	@Test
	public void testGetCache() {
		
		Cache<String, String> cache = CacheFactory.getCache();
		
		assertNotNull("Unable to obtain a cache instance", cache);		
	}
	
	@Test
	public void testGetNammedCache() {
		
		Cache<String, String> cache = CacheFactory.getCache("myCache");
		
		assertNotNull("Unable to obtain a named cache instance", cache);		
	}
	
	@Test
	public void testGetObjectFromCache() {
		
		Cache<String, String> cache = CacheFactory.getCache();
		
		String key = "key";
		// put in cache
		cache.put(key, "myValue");
		// get from cache
		String cachedValue = cache.get(key);
		
		assertNotNull("Unable to get a cached object", cachedValue);		
	}
	
	@Test
	public void testGetObjectFromCacheWithTTL() throws InterruptedException {
		
		TTLCache<String, String> ttlCache = CacheFactory.getTTLCache("ttlCache");
		
		// set ttl of 2 seconds
		ttlCache.setTtlInMs(2000);
		
		String key = "key";
		// put in cache
		ttlCache.put(key, "myValue");
		// get from cache
		String cachedValue = ttlCache.get(key);
		assertNotNull("Unable to get a cached object", cachedValue);
		
		Thread.sleep(2500);
		
		cachedValue = ttlCache.get(key);
		assertNull("The cached object doesn't have to be available", cachedValue);
	}
	
}
