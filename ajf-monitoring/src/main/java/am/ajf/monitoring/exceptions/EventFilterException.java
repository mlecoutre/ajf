package am.ajf.monitoring.exceptions;

public class EventFilterException extends EventException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public EventFilterException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public EventFilterException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public EventFilterException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public EventFilterException(Throwable cause) {
		super(cause);
	}
	
	

}
