package am.ajf.core.cache;

public interface CacheManagerAdapter extends CacheManager {

	/**
	 * return the CacheManager delegate
	 * @return
	 */	
	Object getDelegate();
	
}
