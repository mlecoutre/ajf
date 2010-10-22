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

	private String errorType;

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
		super(message, cause);
		this.errorType = errorType;

	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

}
