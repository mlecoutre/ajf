package am.ajf.monitoring.exceptions;

public class EventEmitterException extends EventException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public EventEmitterException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public EventEmitterException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public EventEmitterException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public EventEmitterException(Throwable cause) {
		super(cause);
	}
	
	

}
