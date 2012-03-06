package am.ajf.core.configuration;

import am.ajf.core.ApplicationContext;
import ch.qos.logback.core.PropertyDefinerBase;

/**
 * ConfigurationPropertyDefiner
 * SLF4J component which allow to resolve application config entries
 * in the logging configuration file
 * @author U002617
 *
 */
public class ConfigurationPropertyDefiner extends PropertyDefinerBase {

	private String propertyKey = null;

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
		if (ApplicationContext.isInitialized()) {
			String value = null;
			if (null == propertyKey) {
				value = ApplicationContext.getLogDir().getPath();
			} else {
				value = ApplicationContext.getConfiguration().getString(
					propertyKey);
			}
			return value;
		}
		
		return null;
	}

}
