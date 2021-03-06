package am.ajf.web;

import am.ajf.core.logger.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;

/**
 * AJF web util class Can be used as static import.
 * 
 * @author E010925
 * 
 */
public class WebUtils {

	// private FacesContext context;
	private static String bundleName;

	private static Logger log = LoggerFactory.getLogger(WebUtils.class);

	/**
	 * Stops creation of a new WebUtils object.
	 */
	private WebUtils() {
		super();
	}

	/**
	 * Use the default bundle message defined in the web.xml file
	 * 
	 * @param fieldName
	 *            i18n key
	 * @return i18n associated value regarding the user Locale
	 */
	public static String getFieldLabel(String fieldName) {
		String bundleName = FacesContext.getCurrentInstance().getApplication()
				.getMessageBundle();
		return getFieldLabel(fieldName, bundleName,
				FacesContext.getCurrentInstance());
	}

	/**
	 * Get the field label.
	 * 
	 * @param fieldName
	 *            fieldName
	 * @param bundleName
	 *            resource bundle name
	 * @param context
	 *            FaceContext object
	 * @return Message from the Message Source.
	 */
	public static String getFieldLabel(String fieldName, String bundleName,
			FacesContext context) {

		Locale locale = context.getViewRoot().getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale,
				getClassLoader());

		String label = null;
		try {
			label = bundle.getString(fieldName);
			return label;
		} catch (MissingResourceException e) {
			log.warn(String.format("Bundle missing: %s, %s", bundleName,
					e.getMessage()));
		}

		try {
			/** Look for just fieldName, e.g., firstName. */
			label = bundle.getString(fieldName);
		} catch (MissingResourceException e) {
			/**
			 * Convert fieldName, e.g., firstName automatically becomes First
			 * Name.
			 */
			label = generateLabelValue(fieldName);
		}

		return label;

	}

	private static ClassLoader getClassLoader() {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (classLoader == null) {
			return WebUtils.class.getClassLoader();
		}
		return classLoader;
	}

	/**
	 * Generate the field in an acceptable label
	 * <ul>
	 * <li>Transforms first.name into First name.</li>
	 * <li>Transforms firstName into First name.</li>
	 * </ul>
	 * This allows reasonable defaults for labels.
	 * 
	 * @param fieldName
	 *            fieldName
	 * 
	 * @return generated label name.
	 */
	public static String generateLabelValue(String fieldName) {

		StringBuffer buffer = new StringBuffer(fieldName.length() * 2);
		char[] chars = fieldName.toCharArray();

		/* Change firstName to First Name. */
		for (int index = 0; index < chars.length; index++) {
			char cchar = chars[index];

			/* Make the first character uppercase. */
			if (index == 0) {
				cchar = Character.toUpperCase(cchar);
				buffer.append(cchar);

				continue;
			}
			/* Look for an uppercase character, if found add a space. */
			if (cchar == '.') {
				buffer.append(' ');
				continue;
			}

			/* Look for an uppercase character, if found add a space. */
			if (Character.isUpperCase(cchar)) {
				buffer.append(' ');
				buffer.append(Character.toLowerCase(cchar));

				continue;
			}

			buffer.append(cchar);
		}

		return buffer.toString();
	}

	/**
	 * This method has to be used in each method of ManagedBean. It's create log
	 * message, trace the exception and create a standard face message returned
	 * to the application user.
	 * 
	 * @param e
	 *            exception
	 * @param log
	 *            targeted logger
	 * @param message
	 *            detail message for the Face Context
	 */
	public static void handleError(Exception e, Logger log, String message) {
		handleError(e, log, null, message);
	}

	/**
	 * Manage handle error for JSF web components
	 * 
	 * @param e
	 *            exception
	 * @param log
	 *            target logger
	 * @param clientId
	 *            target client ID for growl or messages
	 * @param message
	 *            messages to display
	 */
	public static void handleError(Exception e, Logger log, String clientId,
			String message) {
		log.error(message, e);
		FacesMessage facesMessage = new FacesMessage(
				FacesMessage.SEVERITY_ERROR,
				getFieldLabel("application.error.occured"), message);
		FacesContext.getCurrentInstance().addMessage(clientId, facesMessage);
	}

	/**
	 * 
	 * @return bundleName property
	 */
	public static String getBundleName() {
		return bundleName;
	}

	/**
	 * 
	 * @param bundleName
	 *            bundleName to set
	 */
	public static void setBundleName(String bundleName) {
		WebUtils.bundleName = bundleName;
	}

	/**
	 * 
	 * @param key
	 *            key in the configuration file
	 * @param configFilename
	 *            configuration file name
	 * @return value associated with the key
	 */
	public static String givePropertyValue(String key, String configFilename) {
		try {
			File configFile = new File(WebUtils.class.getClassLoader()
					.getResource(configFilename).getFile());
			Properties prop = new Properties();

			InputStream is = new FileInputStream(configFile);

			prop.load(is);
			String value = prop.getProperty(key);
			if (value == null) {
				log.warn(String
						.format("Null value in the config file %s or key does not exist.",
								configFilename));

				log.warn(String.format(
						"Default configuration return the key value %s", key));

				return key;
			} else {
				return value;
			}
		} catch (Exception e) {
			log.warn(String.format(
					" Problem to access to the key %s in the config file %s",
					key, configFilename));
			log.warn(" Default configuration return the key value " + key);
			return key;
		}
	}
}