package foo.lib.business;

import am.ajf.core.services.exceptions.BusinessLayerException;

public interface MyBD  {
	public enum ErrorHandlingCase {
		OK, ERROR
	};

	public String testErrorHandling(ErrorHandlingCase param)
			throws BusinessLayerException;
}
