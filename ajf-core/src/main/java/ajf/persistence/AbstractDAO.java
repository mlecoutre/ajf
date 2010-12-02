package ajf.persistence;


public abstract class AbstractDAO implements DAO {

	protected int firstResult = -1;
	protected int maxResults = -1;
	
	/*
	 * @see adc.persistence.DAO#getFirstResult()
	 */
	public int getFirstResult() {
		return firstResult;
	}

	/*
	 * @see adc.persistence.DAO#setFirstResult(int)
	 */
	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	/*
	 * @see adc.persistence.DAO#getMaxResults()
	 */
	public int getMaxResults() {
		return maxResults;
	}

	/*
	 * @see adc.persistence.DAO#setMaxResults(int)
	 */
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}
	
	
}
