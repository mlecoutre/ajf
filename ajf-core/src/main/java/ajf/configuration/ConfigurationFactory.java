package ajf.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * 
 * @author vincent
 * 
 */
public class ConfigurationFactory {

	private ConfigurationFactory() {
		super();
	}

	/***
	 * create Configuration object form resource
	 * @param resourceName
	 * @return
	 * @throws FileNotFoundException
	 * @throws ConfigurationException
	 */
	public static Configuration getConfigurationFromResource(String resourceName)
			throws FileNotFoundException, ConfigurationException {

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream(resourceName);

		if (null == is)
			throw new FileNotFoundException("Unable to find resource '"
					+ resourceName + "'.");

		PropertiesConfiguration pConfig = new PropertiesConfiguration();
		pConfig.load(is);

		try {
			is.close();
		}
		catch (IOException e) {
			// Nothing to do
		}
		is = null;

		return pConfig;

	}

	/**
	 * create Configuration object form file
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 * @throws ConfigurationException
	 */
	public static Configuration getConfigurationFromFile(String filePath)
			throws FileNotFoundException, ConfigurationException {

		File propertieFile = new File(filePath);
		if (!propertieFile.exists()) {
			throw new FileNotFoundException("Unable to find the file '"
					+ filePath + "'.");
		}

		PropertiesConfiguration pConfig = new PropertiesConfiguration();
		pConfig.load(propertieFile);

		propertieFile = null;

		return pConfig;

	}

}
