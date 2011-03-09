package ajf.cache;

public interface Cache {

	/**
	 * put a new object in the cache
	 * @param key
	 * @param value
	 */
	void put(Object key, Object value);
	
	/**
	 * get an object from the cache
	 * @param key
	 * @return
	 */
	Object get(Object key);
	
	/**
	 * remove an object from the cache
	 * @param key
	 */
	void remove(Object key);
	
	/**
	 * is the cache contain an object
	 * @param key
	 * @return
	 */
	boolean contains(Object key);
	
	/**
	 * clear the cache
	 */
	void clear();
	
	/**
	 * is the cache empty 
	 * @return
	 */
	boolean isEmpty();
	
}
