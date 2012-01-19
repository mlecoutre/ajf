package am.ajf.core.cache;

import java.util.Collection;

public interface Cache {

	/**
	 * 
	 * @return the cache name
	 */
	String getName();
	
	/**
	 * @param key
	 * @return true if the key exist in the cache
	 */
	boolean exist(Object key);

	/**
	 * @param key
	 * @return the cached value for entry 'key'
	 */
	Object get(Object key);

	/**
	 * store a new value in the cache
	 * 
	 * @param key
	 * @param value
	 */
	void put(Object key, Object value);

	/**
	 * @return the keys collections
	 */
	Collection<Object> keys();

	/**
	 * remove a cached entry
	 * 
	 * @param key
	 * @return
	 */
	Object remove(Object key);

	/**
	 * clear the entire cache
	 */
	void clear();

}
