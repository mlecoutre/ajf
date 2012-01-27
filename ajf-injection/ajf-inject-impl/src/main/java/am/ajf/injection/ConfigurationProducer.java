package am.ajf.injection;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.commons.configuration.Configuration;

import am.ajf.core.ApplicationContext;

public class ConfigurationProducer {

	public ConfigurationProducer() {
		super();
	}

	@Produces
	public Configuration produceConfiguration(InjectionPoint ip) {
		
		Configuration config = ApplicationContext.getConfiguration();
		return config;
		
	}
	
}
