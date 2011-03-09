package ajf.cache;

public interface TTLCache extends Cache {

	/**
	 * @return the ttlInMs
	 */
	long getTtlInMs();

	/**
	 * @param ttlInMs the ttlInMs to set
	 */
	void setTtlInMs(long ttlInMs);
	
}
