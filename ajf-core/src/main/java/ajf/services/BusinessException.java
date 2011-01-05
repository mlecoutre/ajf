package ajf.services;

public class BusinessException extends ServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BusinessException() {
		super();
	}

	public BusinessException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(Throwable throwable) {
		super(throwable);
	}
	
	
	
}
