package foo.core.services;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import am.ajf.core.services.exceptions.ServiceLayerException;
import am.ajf.injection.Cached;
import am.ajf.injection.ErrorHandled;
import am.ajf.injection.Property;
import foo.lib.services.MyServiceBD;

@ApplicationScoped
public class MyService extends AbstractService implements MyServiceBD {

	@Inject
	@Property("myKey")
	private String myKeyValue;

	@Inject
	@Property("mySecondKey")
	private String mySecondKeyValue;

	/**
	 * Default constructor
	 */
	public MyService() {
		super();
	}

	@PostConstruct
	public void init() {
		logger.debug("Initalized");
		logger.info("myKeyValue = {}", myKeyValue);
		logger.info("mySecondKeyValue = {}", mySecondKeyValue);
	}

	@PreDestroy
	public void cleanup() {
		logger.debug("Terminated");
	}

	public String getMyKeyValue() {
		return myKeyValue;
	}

	public void setMyKeyValue(String myKeyValue) {
		this.myKeyValue = myKeyValue;
	}

	public String getMySecondKeyValue() {
		return mySecondKeyValue;
	}

	public void setMySecondKeyValue(String mySecondKeyValue) {
		this.mySecondKeyValue = mySecondKeyValue;
	}

	@Override
	@Cached(cacheProvider = "threadlocal")
	public String myFirstOperation(String string1, String string2) {
		String res = "Hello ".concat(string1).concat(", ").concat(string2);
		logger.debug("Process: ".concat(res));
		return res;
	}

	/**
	 * Automatically annotated by ServicesExtension {@link ErrorHandled}
	 */
	public String myErrorHandlingMethod(ErrorHandlingCase param)
			throws ServiceLayerException {
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
