package ajf.persistence;


public class PersistenceUnitDesc {

	private String name;
	private String transactionType;

	/**
	 * @param name
	 * @param transactionType
	 */
	public PersistenceUnitDesc(String name, String transactionType) {
		super();
		this.name = name;
		this.transactionType = transactionType;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the transactionType
	 */
	public String getTransactionType() {
		return transactionType;
	}

	/**
	 * @param transactionType the transactionType to set
	 */
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	
}
