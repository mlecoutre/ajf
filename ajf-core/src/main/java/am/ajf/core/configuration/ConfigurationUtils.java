package am.ajf.core.configuration;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertyConverter;

public class ConfigurationUtils {
	
	private ConfigurationUtils() {
		super();
	}

	/**
	 * Evaluate the expression in the configuration context
	 * @param expression
	 * @param configuration
	 * @return
	 * @throws ConfigurationException
	 */
	public static Object evaluate(String expression,
			Configuration configuration) throws ConfigurationException {
		
		if (!(configuration instanceof AbstractConfiguration)) {
			throw new ConfigurationException("The 'configuration' must extend the abstract class '".concat(AbstractConfiguration.class.getName()).concat("'."));
		}
		
		AbstractConfiguration abstractConfiguration = (AbstractConfiguration) configuration;		
		Object value = PropertyConverter.interpolate(expression, abstractConfiguration);
		return value;
	}

	/**
	 * Evaluate the EL expression in the configuration context
	 * 
	 * @param expression
	 * @param configuration
	 * @return
	 * @throws ELException
	 */
	/*
	public static Object evaluateExpression(String expression,
			Configuration configuration) throws ELException {

		ExpressionEvaluator exprEvaluator = new ExpressionEvaluatorImpl();
		// Create the variable resolver
		VariableResolver variableResolver = new VariableResolver() {

			@Override
			public Object resolveVariable(String pName) throws ELException {
				Object res = configuration.getString(pName)
				return res;
			}

		};
				
		Object value = exprEvaluator.evaluate(expression, Object.class, variableResolver, null);
		return value;
		
	}
	*/

}
