package foo.web.controllers;

public class MyMBean {
	public enum ErrorHandlingCase {
		OK, ERROR
	};

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
