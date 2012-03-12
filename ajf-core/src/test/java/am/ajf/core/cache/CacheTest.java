package am.ajf.core.cache;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.Set;

import org.junit.Test;

public class CacheTest {

	@Test
	public void testGetCache() {
		
		Cache cache = CacheManagerFactory.getFirstCacheManager().getCache();
		
		assertNotNull("Unable to obtain a cache instance", cache);		
	}
	
	@Test
	public void testGetNammedCache() {
		
		Cache cache = CacheManagerFactory.getFirstCacheManager().getCache("myCache");
		
		assertNotNull("Unable to obtain a named cache instance", cache);		
	}
	
	@Test
	public void testGetNammedCacheManager() {
		
		Set<String> names = CacheManagerFactory.getCacheManagerNames();
		for (String name : names) {
			CacheManager cacheManager = CacheManagerFactory.getCacheManager(name);
			assertNotNull("Unable to obtain a named cache manager instance", cacheManager);
		}
			
	}
	
	@Test
	public void testGetCacheManagerNames() {
		
		Collection<String> names = CacheManagerFactory.getCacheManagerNames();
		for (String name : names) {
			System.out.println(name);
		}
		
	}
	
	@Test
	public void testGetObjectFromCache() {
		
		Cache cache = CacheManagerFactory.getFirstCacheManager().getCache();
		
		String key = "key";
		// put in cache
		cache.put(key, "myValue");
		// get from cache
		String cachedValue = (String) cache.get(key);

		assertNotNull("Unable to get the default cached object", cachedValue);		
	}
	
	@Test
	public void testGetObjectFromCacheWithTTL() throws InterruptedException {
		
		Cache ttlCache = CacheManagerFactory.getFirstCacheManager().getCache("ttlCache", 2000);
		
		String key = "key";
		// put in cache
		ttlCache.put(key, "myValue");
		// get from cache
		String cachedValue = (String) ttlCache.get(key);
		assertNotNull("Unable to get a cached object", cachedValue);
		
		Thread.sleep(2500);
		
		cachedValue = (String) ttlCache.get(key);
		assertNull("The cached object doesn't have to be available", cachedValue);
	}
	
}
