package ajf.persistence;


public interface DAO {
	
	/**
	 * @return the firstResult
	 */
	int getFirstResult();

	/**
	 * @param firstResult the firstResult to set
	 */
	void setFirstResult(int firstResult);

	/**
	 * @return the maxResults
	 */
	int getMaxResults();

	/**
	 * @param maxResults the maxResults to set
	 */
	void setMaxResults(int maxResults);
	
}
