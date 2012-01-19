package am.ajf.core.configuration;

import am.ajf.core.ApplicationContext;
import ch.qos.logback.core.PropertyDefinerBase;

public class ConfigurationPropertyDefiner extends PropertyDefinerBase {

	private static final String APPLICATION_LOG_DIR_KEY = "log.dir";
	
	private String propertyKey = APPLICATION_LOG_DIR_KEY;
	
	public ConfigurationPropertyDefiner() {
		super();
		ApplicationContext.init();
	}

	public String getPropertyKey() {
		return propertyKey;
	}

	public void setPropertyKey(String property) {
		this.propertyKey = property;
	}

	@Override
	public String getPropertyValue() {
		String value = ApplicationContext.getConfiguration().getString(propertyKey);
		return value;
	}
	
}
