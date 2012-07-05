package foo;

import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

public class App2 {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		String configFile = "infinispan_config.xml";
		DefaultCacheManager cacheManager = new DefaultCacheManager(configFile);
		//cacheManager.start();
		Cache<String, String> cache = cacheManager.getCache();
		
		String key = "aKey";
		cache.put(key, "test");
		
		String value = cache.get(key);
		cache.remove(key);
		System.out.println(value);
		
		cacheManager.stop();

	}

}
