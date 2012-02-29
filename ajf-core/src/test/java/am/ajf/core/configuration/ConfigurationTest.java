package am.ajf.core.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
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

	


	/**
	 * Test configuration
	 * @throws Exception on error
	 */
	@Test
	public void testConfig() throws Exception {

		CompositeConfiguration configuration = new CompositeConfiguration();

		AbstractConfiguration pConfig = new PropertiesConfiguration();
		pConfig.addProperty("b", "${a}_26");
		pConfig.addProperty("a", "25");
		configuration.addConfiguration(pConfig);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "value");
		map.put("c", "${b}");

		List<String> list = new ArrayList<String>();
		list.add("x");
		list.add("y");
		list.add("z");
		map.put("d", list);

		AbstractConfiguration bConfig = new BeanConfiguration();
		bConfig.addProperty("aMap", map);

		configuration.addConfiguration(bConfig);

		logger.info(configuration.getString("a"));

		List<String> keys = new ArrayList<String>();
		keys.add("b");
		pConfig.addConfigurationListener(new FilteredConfigurationListener(
				keys, this));

		pConfig.setProperty("b", "${a} =? ${a}");
		logger.info(pConfig.getString("b"));
		pConfig.clearProperty("b");
		pConfig.clear();

		logger.info(configuration.getString("a"));
		logger.info(configuration.getString("b"));
		logger.info(configuration.getString("aMap.[name]"));
		logger.info(configuration.getString("aMap.[c]"));
		logger.info(configuration.getString("aMap.[d](0)"));

		logger.info(""
				+ ConfigurationUtils.evaluate("the value is : ${aMap.[name]}",
						configuration));

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

		Configuration configuration = new BaseConfiguration();

		String res = (String) ConfigurationUtils.evaluate("${sys:java.home}",
				configuration);
		System.out.println(res);

		res = (String) ConfigurationUtils.evaluate("${env:JAVA_HOME}",
				configuration);
		System.out.println(res);

		res = (String) ConfigurationUtils.evaluate(
				"${const:am.ajf.core.configuration.ConfigurationTest.NAME}",
				configuration);
		System.out.println(res);

	}

}
