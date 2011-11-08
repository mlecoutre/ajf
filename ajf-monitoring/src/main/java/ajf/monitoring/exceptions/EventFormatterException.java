package ajf.monitoring.exceptions;

public class EventFormatterException extends EventException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public EventFormatterException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public EventFormatterException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public EventFormatterException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public EventFormatterException(Throwable cause) {
		super(cause);
	}
	
	

}
