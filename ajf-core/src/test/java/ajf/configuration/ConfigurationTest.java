package ajf.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import org.slf4j.Logger;

import ajf.logger.LoggerFactory;

public class ConfigurationTest {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger();

	public ConfigurationTest() {
	}

	@Test
	public void testConfig() {

		CompositeConfiguration configuration = new CompositeConfiguration();

		{
			Configuration config = new PropertiesConfiguration();
			config.addProperty("b", "${a}_26");
			config.addProperty("a", "25");
			configuration.addConfiguration(config);
		}
		
		{
			BeanConfiguration config = new BeanConfiguration();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", "value");
			map.put("c", "${b}");
			
			List<String> list = new ArrayList<String>();
			list.add("x");
			list.add("y");
			list.add("z");
			map.put("d", list);
			
			config.addProperty("aMap", map);
			
			configuration.addConfiguration(config);
			
		}
				
		System.out.println(configuration.getString("a"));
		System.out.println(configuration.getString("b"));
		System.out.println(configuration.getString("aMap.[name]"));
		System.out.println(configuration.getString("aMap.[c]"));
		System.out.println(configuration.getString("aMap.[d](1)"));
		
		System.out.println(ConfigurationUtils.evaluate("the value is : ${aMap.[name]}", configuration));
				
	}

}
