package am.ajf.injection;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerProducer {
	
	public LoggerProducer() {
		super();
	}
	
	@Produces
	public Logger produceLogger(InjectionPoint ip) {
		Class<?> clazz = ip.getBean().getBeanClass();
		Logger logger = LoggerFactory.getLogger(clazz);
		return logger;
	}

}
