package am.ajf.core.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * 
 * @author vincent
 * 
 */
public class ConfigurationHelper {

	private ConfigurationHelper() {
		super();
	}

	/***
	 * create Configuration object from a properties resource
	 * 
	 * @param resourceName
	 * @return
	 * @throws FileNotFoundException
	 * @throws ConfigurationException
	 */
	public static AbstractConfiguration newConfigurationFromResource(String resourceName)
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
	 * create Configuration object from XML resource
	 * 
	 * @param resourceName
	 * @return
	 * @throws FileNotFoundException
	 * @throws ConfigurationException
	 */
	public static AbstractConfiguration newConfigurationFromXMLResource(
			String resourceName) throws FileNotFoundException,
			ConfigurationException {

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream(resourceName);

		if (null == is)
			throw new FileNotFoundException("Unable to find resource '"
					+ resourceName + "'.");

		XMLConfiguration xConfig = new XMLConfiguration();
		xConfig.load(is);

		try {
			is.close();
		}
		catch (IOException e) {
			// Nothing to do
		}
		is = null;

		return xConfig;

	}

	/**
	 * create Configuration object form file
	 * 
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 * @throws ConfigurationException
	 */
	public static AbstractConfiguration newConfigurationFromFile(String filePath)
			throws FileNotFoundException, ConfigurationException {

		File file = new File(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException("Unable to find the file '"
					+ filePath + "'.");
		}
				
		PropertiesConfiguration pConfig = new PropertiesConfiguration(file);
		return pConfig;

	}
	
	/**
	 * create Configuration object form file
	 * 
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 * @throws ConfigurationException
	 */
	public static AbstractConfiguration newConfigurationFromXMLFile(String filePath)
			throws FileNotFoundException, ConfigurationException {

		File file = new File(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException("Unable to find the XML file '"
					+ filePath + "'.");
		}
				
		XMLConfiguration xConfig = new XMLConfiguration(file);
		return xConfig;

	}
	
	/**
	 * create a merged configuration - CompositeConfiguration
	 * @param configurations
	 * @return
	 */
	public static AbstractConfiguration mergeConfigurations(Configuration...configurations) {
		
		if ((null == configurations) || (0 == configurations.length))
			return null;
		CompositeConfiguration composite = new CompositeConfiguration(Arrays.asList(configurations));  
		return composite;
		
	}
	


}
