package am.ajf.core.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.junit.Test;
import org.slf4j.Logger;

import am.ajf.core.configuration.impl.BeanConfiguration;
import am.ajf.core.logger.LoggerFactory;

/**
 * Configuration test
 * 
 * @author E010925
 * 
 */
public class ConfigurationTest implements PropertyListener {

	public static final String NAME = "name";
	private Logger logger = LoggerFactory.getLogger(ConfigurationTest.class);

	@Test
	public void testMergedConfig() throws Exception {

		// create th new Configuration object from resource
		// 'settings.properties'
		PropertiesConfiguration sConfig = new PropertiesConfiguration();
		sConfig.addProperty("c", "${b}");

		PropertiesConfiguration pConfig = new PropertiesConfiguration();
		pConfig.addProperty("b", "${a}_26");
		pConfig.addProperty("a", "25");

		// merge (chain) the configurations objects
		Configuration config = ConfigurationHelper.mergeConfigurations(sConfig,
				pConfig);

		// get 'c' property as String
		logger.info(config.getString("c"));

	}

	@Test
	public void testPropertiesConfig() throws Exception {

		PropertiesConfiguration pConfig = new PropertiesConfiguration();
		pConfig.addProperty("b", "${a}_26");
		pConfig.addProperty("a", "25");

		logger.info(pConfig.getString("a"));
		logger.info(pConfig.getString("b"));
	}

	@Test
	public void testBeanConfig() throws Exception {

		Configuration bConfig = populateObjectConfig();

		System.out.println(bConfig.getString("aMap.[name]"));
		System.out.println(bConfig.getString("aMap.[d](0)"));
			

	}

	public Configuration populateObjectConfig() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "value");

		List<String> list = new ArrayList<String>();
		list.add("x");
		list.add("y");
		list.add("z");
		map.put("d", list);

		BeanConfiguration bConfig = new BeanConfiguration();
		bConfig.addProperty("aMap", map);
		
		return bConfig;
	}

	@Test
	public void testEvaluateInConfig() throws Exception {

		Configuration bConfig = populateObjectConfig();
		
		logger.info(""
				+ ConfigurationUtils.evaluate("the value is : ${aMap.[name]}",
						bConfig));

	}

	@Override
	public void configurationChanged(EnumEventType eventType,
			Configuration source, String key, Object oldValue, Object newValue) {

		System.out.println("[" + eventType.getReadableValue() + "] - " + key
				+ " (" + oldValue + ") = " + newValue);

	}

	@Override
	public void configurationClear(EnumEventType enum1, Configuration source) {

		System.out.println("Configuration cleaned");

	}

	@Test
	public void testSystemConfig() {

		Configuration configuration = new SystemConfiguration();
		String value = configuration.getString("java.home");
		System.out.println(value);

	}

	@Test
	public void testPrefixedVarsInterpolation() {

		Configuration configuration = new PropertiesConfiguration();

		String res = (String) ConfigurationUtils.evaluate("${sys:java.home}",
				configuration);
		logger.info(res);

		res = (String) ConfigurationUtils.evaluate("${env:JAVA_HOME}",
				configuration);
		logger.info(res);

		res = (String) ConfigurationUtils.evaluate(
				"${const:am.ajf.core.configuration.ConfigurationTest.NAME}",
				configuration);
		logger.info(res);

	}

}
