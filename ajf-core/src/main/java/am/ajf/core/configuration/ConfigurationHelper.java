package am.ajf.core.configuration;

import am.ajf.core.logger.LoggerFactory;

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
import org.slf4j.Logger;



/**
 * ConfigurationHelper
 * 
 * @author U002617
 * 
 */
public class ConfigurationHelper {

	private static Logger logger = LoggerFactory
			.getLogger(ConfigurationHelper.class);

	/**
	 * Default constructor
	 */
	private ConfigurationHelper() {
		super();
	}

	/***
	 * create Configuration object from a properties resource
	 * 
	 * @param resourceName
	 *            resourceName
	 * @return Configuration
	 * @throws FileNotFoundException
	 *             resource not found
	 * @throws ConfigurationException
	 *             problem during configuration
	 */
	public static Configuration newConfigurationFromPropertiesResource(
			String resourceName) throws FileNotFoundException,
			ConfigurationException {

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream(resourceName);

		if (null == is) {
			// 19455: get Value from settings.properties should not throw
			// exception
			// http://bugtracking.arcelor.net/show_bug.cgi?id=19455
			// throw new FileNotFoundException("Unable to find resource '"
			// + resourceName + "'.");

			logger.warn(String.format("Unable to find resource '%s'",
					resourceName));
		}

		PropertiesConfiguration pConfig = new PropertiesConfiguration();
		pConfig.load(is);

		try {
			is.close();
		} catch (IOException e) {
			// Nothing to do
			logger.warn(String.format(
					"Does not succeed to close correcly IS %s", resourceName));
		}
		is = null;

		return pConfig;

	}

	/**
	 * create Configuration object from a ini resource
	 * 
	 * @param resourceName
	 *            name of the resource
	 * @return Configuration
	 * @throws FileNotFoundException
	 *             resource not found
	 * @throws ConfigurationException
	 *             problem during configuration
	 */
	public static Configuration newConfigurationFromIniResource(
			String resourceName) throws FileNotFoundException,
			ConfigurationException {

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream(resourceName);

		if (null == is) {
			throw new FileNotFoundException("Unable to find resource '"
					+ resourceName + "'.");
		}
		HierarchicalINIConfiguration pConfig = new HierarchicalINIConfiguration();
		pConfig.load(is);

		try {
			is.close();
		} catch (IOException e) {
			// Nothing to do
			logger.warn(String.format(
					"Does not succeed to close correcly IS %s", resourceName));
		}
		is = null;

		return pConfig;

	}

	/**
	 * create Configuration object from a XML resource
	 * 
	 * @param resourceName
	 *            name of the resource
	 * @return Configuration
	 * @throws FileNotFoundException
	 *             resource not found
	 * @throws ConfigurationException
	 *             problem during configuration
	 */
	public static Configuration newConfigurationFromXMLResource(
			String resourceName) throws FileNotFoundException,
			ConfigurationException {

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream(resourceName);

		if (null == is) {
			// 19455: get Value from settings.properties should not throw
			// exception
			// http://bugtracking.arcelor.net/show_bug.cgi?id=19455
			// throw new FileNotFoundException("Unable to find resource '"
			// + resourceName + "'.");

			logger.warn(String.format("Unable to find resource '%s'",
					resourceName));
		}
		XMLConfiguration xConfig = new XMLConfiguration();
		xConfig.load(is);

		try {
			is.close();
		} catch (IOException e) {
			// Nothing to do
			logger.warn(String.format(
					"Does not succeed to close correcly IS %s", resourceName));
		}
		is = null;

		return xConfig;

	}

	/**
	 * create Configuration object form file
	 * 
	 * @param filePath
	 *            filePath
	 * @return Configuration
	 * @throws FileNotFoundException
	 *             resource not found
	 * @throws ConfigurationException
	 *             problem during configuration
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
	 * @return Configuration
	 * @throws FileNotFoundException
	 *             resource not found
	 * @throws ConfigurationException
	 *             problem during configuration
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
	 *            filePath
	 * @return Configuration
	 * @throws FileNotFoundException
	 *             resource not found
	 * @throws ConfigurationException
	 *             problem during configuration
	 */
	public static Configuration newConfigurationFromIniFile(String filePath)
			throws FileNotFoundException, ConfigurationException {

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
	 *            filePath
	 * @return Configuration
	 * @throws FileNotFoundException
	 *             resource not found
	 * @throws ConfigurationException
	 *             problem during configuration
	 */
	public static Configuration newConfigurationFromXMLFile(String filePath)
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
	 * 
	 * @param configurations
	 *            list of configuration to merge
	 * @return the merge Configuration
	 */
	public static Configuration mergeConfigurations(
			Configuration... configurations) {

		if ((null == configurations) || (0 == configurations.length)) {
			return null;
		}
		CompositeConfiguration composite = new CompositeConfiguration(
				Arrays.asList(configurations));
		return composite;

	}

}
