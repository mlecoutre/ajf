/**
 * 
 */
package ajf.persistence.exception;

/**
 * @author E010925
 * 
 */
@SuppressWarnings("serial")
public class PersistenceLayerException extends Exception {

	/**
	 * default constructor
	 */
	public PersistenceLayerException() {
		super();
	}

	/**
	 * @param message
	 *            error message
	 */
	public PersistenceLayerException(String arg0) {
		super(arg0);

	}

	/**
	 * @param cause
	 *            inner exception
	 */
	public PersistenceLayerException(Throwable cause) {
		super(cause);

	}

	/**
	 * @param message
	 * @param cause
	 *            inner exception
	 */
	public PersistenceLayerException(String message, Throwable cause) {
		super(message, cause);

	}

}
