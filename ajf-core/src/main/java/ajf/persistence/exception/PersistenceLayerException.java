/**
 * 
 */
package ajf.persistence.exception;

import ajf.exceptions.AbstractLayerException;

/**
 * @author E010925
 * 
 */
public class PersistenceLayerException extends AbstractLayerException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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
	public PersistenceLayerException(String message) {
		super(message);

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

	/**
	 * @param message
	 * @param errorType
	 *            type of error
	 * 
	 * @param cause
	 *            inner exception
	 */
	public PersistenceLayerException(String message, String errorType,
			Throwable cause) {
		super(message, errorType, cause);

	}

}
