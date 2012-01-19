package am.ajf.monitoring.exceptions;


public class DuplicateEventTypeException extends EventException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DuplicateEventTypeException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DuplicateEventTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public DuplicateEventTypeException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DuplicateEventTypeException(Throwable cause) {
		super(cause);
	}
	
	

}
