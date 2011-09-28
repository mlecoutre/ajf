package ajf.configuration;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.PropertyConverter;

public abstract class ConfigurationUtils {

	/**
	 * Evaluate the expression in the configuration context
	 * 
	 * @param expression
	 * @param config
	 * @return
	 */
	public static Object evaluate(String expression,
			AbstractConfiguration configuration) {
		Object value = PropertyConverter.interpolate(expression, configuration);
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
