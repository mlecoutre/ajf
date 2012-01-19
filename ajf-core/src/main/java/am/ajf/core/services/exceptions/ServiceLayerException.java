package am.ajf.core.services.exceptions;

import am.ajf.core.exceptions.AbstractLayerException;

public class ServiceLayerException extends AbstractLayerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceLayerException() {
		super();
	}

	public ServiceLayerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceLayerException(String message) {
		super(message);
	}

	public ServiceLayerException(Throwable cause) {
		super(cause);
	}
	
	public ServiceLayerException(String message, String errorType,
			Throwable cause) {
		super(message, errorType, cause);
	}
	
	
}
