package am.ajf.core.exceptions;

public abstract class AbstractLayerException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String errorType;
	protected boolean alreadyHandled = false;
	
	public AbstractLayerException() {
		super();
	}

	public AbstractLayerException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public AbstractLayerException(String message, String errorType,
			Throwable cause) {
		super(message, cause);
		this.errorType = errorType;

	}
	
	public AbstractLayerException(String message) {
		super(message);
	}

	public AbstractLayerException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * @return the errorType
	 */
	public String getErrorType() {
		return errorType;
	}

	/**
	 * @param errorType the errorType to set
	 */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	/**
	 * @return the alreadyHandled
	 */
	public boolean isAlreadyHandled() {
		return alreadyHandled;
	}

	/**
	 * @param alreadyHandled the alreadyHandled to set
	 */
	public void setAlreadyHandled(boolean alreadyHandled) {
		this.alreadyHandled = alreadyHandled;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AbstractLayerException [exception=" + getClass().getName()
				+ ", message=" + getMessage() + ", errorType=" + errorType
				+ ", alreadyHandled=" + alreadyHandled + "]";
	}
	

}
