package ajf.cache;

public interface TTLCache<K, V> extends Cache<K, V> {

	/**
	 * @return the ttlInMs
	 */
	long getTtlInMs();

	/**
	 * @param ttlInMs the ttlInMs to set
	 */
	void setTtlInMs(long ttlInMs);
	
}
