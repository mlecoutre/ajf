package ajf.monitoring.exceptions;

public class EventException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public EventException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public EventException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public EventException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public EventException(Throwable cause) {
		super(cause);
	}
	
	

}
