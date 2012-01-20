package foo.core.services;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import am.ajf.injection.Monitored;
import am.ajf.injection.Property;
import foo.lib.services.MyServiceBD;

@Singleton
public class MyService
	extends AbstractService
	implements MyServiceBD {

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
		logger.info(myKeyValue);
		logger.info(mySecondKeyValue);
	}
	
	@PreDestroy
	public void cleanup() {
		logger.debug("Terminated");
	}
	
	public String getMyKeyValue() {
		return myKeyValue;
	}

	public void setMyKeyValue(String myKeyValue) {
		System.out.println("setted myKeyValue");
		this.myKeyValue = myKeyValue;
	}

	public String getMySecondKeyValue() {
		return mySecondKeyValue;
	}

	public void setMySecondKeyValue(String mySecondKeyValue) {
		System.out.println("setted mySecondKeyValue");
		this.mySecondKeyValue = mySecondKeyValue;
	}

	@Monitored
	public String myFirstOperation(String string) {
		String res = "Hello ".concat(string);
		logger.debug("Process: ".concat(res));
		return res;
	}

}