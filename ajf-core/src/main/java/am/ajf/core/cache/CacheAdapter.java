package am.ajf.core.cache;

public interface CacheAdapter extends Cache {

	/**
	 * return the Cache delegate
	 * @return
	 */
	Object getDelegate();
	
}
