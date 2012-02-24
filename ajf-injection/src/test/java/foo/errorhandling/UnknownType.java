package foo.errorhandling;

import am.ajf.injection.ErrorHandled;

public class UnknownType {

	public enum ErrorHandlingCase {
		OK, ERROR
	};

	@ErrorHandled
	public String testMBeanErrorHandling(ErrorHandlingCase param) {
		switch (param) {
		case OK:
			return "ok";
		case ERROR:
			return new Integer(1 / 0).toString();
		default:
			return String.format("don't know what to do man with %s", param);
		}
	}
}
