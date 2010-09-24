package ajf.persistence;

import java.util.Collection;
import java.util.HashSet;

public class PersistenceUnitDesc {

	private String name;
	private String transactionType;
	
	private Collection<String> classes = new HashSet<String>();

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

	/**
	 * @return the classes
	 */
	public Collection<String> getClasses() {
		return classes;
	}

	/**
	 * @param classes the classes to set
	 */
	public void setClasses(Collection<String> classes) {
		this.classes = classes;
	}
	
	/**
	 * 
	 * @param className
	 * @return
	 */
	public PersistenceUnitDesc addClass(String className) {
		this.classes.add(className);
		return this;
	}
	
	
	
}
