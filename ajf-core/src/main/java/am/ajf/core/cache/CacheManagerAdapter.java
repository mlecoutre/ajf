package am.ajf.core.cache;

public interface CacheManagerAdapter extends CacheManager {

	/**
	 * return the CacheManager delegate
	 * @return
	 */	
	Object getDelegate();
	
	/**
	 * allow to add a manual configured native cache in the CacheManager
	 * @param cacheName
	 * @param nativeCache
	 */
	void addNativeCache(String cacheName, Object nativeCache);
		
}
