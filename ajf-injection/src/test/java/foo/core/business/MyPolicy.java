package foo.core.business;

import am.ajf.core.services.exceptions.BusinessLayerException;
import foo.lib.business.MyBD;

public class MyPolicy implements MyBD {

	@Override
	public String testErrorHandling(ErrorHandlingCase param)
			throws BusinessLayerException {
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
