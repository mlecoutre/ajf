package ajf.services.exceptions;

import ajf.exceptions.AbstractLayerException;

public class BusinessLayerException extends AbstractLayerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BusinessLayerException() {
		super();
	}

	public BusinessLayerException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessLayerException(String message) {
		super(message);
	}

	public BusinessLayerException(Throwable cause) {
		super(cause);
	}

	public BusinessLayerException(String message, String errorType,
			Throwable cause) {
		super(message, errorType, cause);
	}
	
}
