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
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
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
	public static Configuration newConfigurationFromPropertiesResource(
			String resourceName) throws FileNotFoundException,
			ConfigurationException {

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
		} catch (IOException e) {
			// Nothing to do
		}
		is = null;

		return pConfig;

	}

	/***
	 * create Configuration object from a ini resource
	 * 
	 * @param resourceName
	 * @return
	 * @throws FileNotFoundException
	 * @throws ConfigurationException
	 */
	public static Configuration newConfigurationFromIniResource(
			String resourceName) throws FileNotFoundException,
			ConfigurationException {

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream(resourceName);

		if (null == is)
			throw new FileNotFoundException("Unable to find resource '"
					+ resourceName + "'.");

		HierarchicalINIConfiguration pConfig = new HierarchicalINIConfiguration();
		pConfig.load(is);

		try {
			is.close();
		} catch (IOException e) {
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
	public static Configuration newConfigurationFromXMLResource(
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
		} catch (IOException e) {
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
	public static Configuration newConfigurationFromPropertiesFile(
			String filePath) throws FileNotFoundException,
			ConfigurationException {

		File file = new File(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException("Unable to find the file '"
					+ filePath + "'.");
		}

		PropertiesConfiguration pConfig = new PropertiesConfiguration(file);
		return pConfig;

	}

	/**
	 * create an new System Configurationobject
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws ConfigurationException
	 */
	public static Configuration newSystemConfiguration()
			throws FileNotFoundException, ConfigurationException {

		AbstractConfiguration pConfig = new SystemConfiguration();
		return pConfig;

	}

	/**
	 * create Configuration object form ini file
	 * 
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 * @throws ConfigurationException
	 */
	public static Configuration newConfigurationFromIniFile(
			String filePath) throws FileNotFoundException,
			ConfigurationException {

		File file = new File(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException("Unable to find the file '"
					+ filePath + "'.");
		}

		HierarchicalINIConfiguration pConfig = new HierarchicalINIConfiguration(
				file);
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
	public static Configuration newConfigurationFromXMLFile(
			String filePath) throws FileNotFoundException,
			ConfigurationException {

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
	 * 
	 * @param configurations
	 * @return
	 */
	public static Configuration mergeConfigurations(
			Configuration... configurations) {

		if ((null == configurations) || (0 == configurations.length))
			return null;
		CompositeConfiguration composite = new CompositeConfiguration(
				Arrays.asList(configurations));
		return composite;

	}

}
