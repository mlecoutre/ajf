package ajf.configuration;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.PropertyConverter;

public abstract class ConfigurationUtils {

	/**
	 * Evaluate the expression in the configuration context
	 * @param expression
	 * @param config
	 * @return
	 */
	public static Object evaluate(String expression, AbstractConfiguration configuration) {
		Object value = PropertyConverter.interpolate(expression, configuration);
		return value;
	}

}
