package am.ajf.notif.feed.rss.model;

public class InvalidRequiredParamException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidRequiredParamException() {
		super();
	}

	public InvalidRequiredParamException(String message) {
		super(message);
	}
}