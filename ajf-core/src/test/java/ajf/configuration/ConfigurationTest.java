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

import ajf.configuration.BeanConfiguration;
import ajf.configuration.ConfigurationUtils;
import ajf.logger.LoggerFactory;

public class ConfigurationTest {

	private static final Logger logger = LoggerFactory.getLogger();

	public ConfigurationTest() {
	}

	@Test
	public void testConfig() throws Exception {

		CompositeConfiguration configuration = new CompositeConfiguration();

		{
			Configuration config = new PropertiesConfiguration();
			config.addProperty("b", "${a}_26");
			config.addProperty("a", "25");
			configuration.addConfiguration(config);
		}
		
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", "value");
			map.put("c", "${b}");
			
			List<String> list = new ArrayList<String>();
			list.add("x");
			list.add("y");
			list.add("z");
			map.put("d", list);
			
			BeanConfiguration config = new BeanConfiguration();
			config.addProperty("aMap", map);
			
			configuration.addConfiguration(config);
			
		}
				
		logger.info(configuration.getString("a"));
		logger.info(configuration.getString("b"));
		logger.info(configuration.getString("aMap.[name]"));
		logger.info(configuration.getString("aMap.[c]"));
		logger.info(configuration.getString("aMap.[d](0)"));
		
		logger.info(""+ConfigurationUtils.evaluate("the value is : ${aMap.[name]}", configuration));
		
						
	}

}
