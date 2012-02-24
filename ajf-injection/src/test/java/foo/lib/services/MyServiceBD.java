package foo.lib.services;

import am.ajf.core.services.exceptions.ServiceLayerException;

public interface MyServiceBD {

	public enum ErrorHandlingCase {
		OK, ERROR
	};

	String myFirstOperation(String string1, String string2);

	String myErrorHandlingMethod(ErrorHandlingCase parameter) throws ServiceLayerException;
}
