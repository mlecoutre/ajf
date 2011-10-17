package ajf.cache;

public interface Cache<K, V> {

	/**
	 * put a new object in the cache
	 * @param key
	 * @param value
	 */
	void put(K key, V value);
	
	/**
	 * get an object from the cache
	 * @param key
	 * @return
	 */
	V get(K key);
	
	/**
	 * remove an object from the cache
	 * @param key
	 */
	V remove(K key);
	
	/**
	 * is the cache contain an object
	 * @param key
	 * @return
	 */
	boolean contains(K key);
	
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
