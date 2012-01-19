package am.ajf.core.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import org.slf4j.Logger;

import am.ajf.core.configuration.ConfigurationUtils;
import am.ajf.core.configuration.EnumEventType;
import am.ajf.core.configuration.FilteredConfigurationListener;
import am.ajf.core.configuration.PropertyListener;
import am.ajf.core.configuration.impl.BeanConfiguration;
import am.ajf.core.logger.LoggerFactory;

public class ConfigurationTest implements PropertyListener {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationTest.class);

	public ConfigurationTest() {
	}

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
		pConfig
				.addConfigurationListener(new FilteredConfigurationListener(
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
	public void configurationChanged(EnumEventType eventType, Configuration source,
			String key, Object oldValue, Object newValue) {

		System.out.println("[" + eventType.getReadableValue() + "] - " + key
				+ " (" + oldValue + ") = " + newValue);

	}

	@Override
	public void configurationClear(EnumEventType enum1, Configuration source) {

		System.out.println("Configuration cleaned");
		
	}

}
