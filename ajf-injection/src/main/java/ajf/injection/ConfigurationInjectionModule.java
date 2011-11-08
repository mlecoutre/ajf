package ajf.injection;

import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;

import ajf.logger.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class ConfigurationInjectionModule extends AbstractModule {
	
	private Logger logger = LoggerFactory.getLogger();
	
	private Configuration configuration = null;
	
	public ConfigurationInjectionModule(Configuration configuration) {
		super();
		this.configuration = configuration;
	}

	@Override
	protected void configure() {
		
		for (Iterator<?> iterator = configuration.getKeys(); iterator.hasNext();) {
			
			try {
				String key = (String) iterator.next();
				bind(String.class).annotatedWith(Names.named(key)).toProvider(new ConfigurationProvider(configuration, key));
			} catch (Exception e) {
				logger.warn("Unattempted exception.", e);
			}
			
		}
		
		bind(Configuration.class).toInstance(configuration);
		
	}

}
