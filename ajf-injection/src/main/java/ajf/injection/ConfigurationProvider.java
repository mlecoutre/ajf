package ajf.injection;

import org.apache.commons.configuration.Configuration;

import com.google.inject.Provider;

public class ConfigurationProvider implements Provider<String> {

	private final Configuration configuration;
	private final String key;
	
	public ConfigurationProvider(Configuration configuration, String key) {
		this.configuration = configuration;
		this.key = key;
	}

	@Override
	public String get() {
		String result = configuration.getString(key);
		return result;
	}
	

}
