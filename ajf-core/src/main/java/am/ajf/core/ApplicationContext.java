package am.ajf.core;

import java.io.File;

import org.apache.commons.configuration.AbstractConfiguration;

import am.ajf.core.configuration.ConfigurationHelper;
import am.ajf.core.utils.ClassPathUtils;

public class ApplicationContext {

	private static final String LOG_CONFIG_TEST = "logback-test.xml";
	private static final String LOG_CONFIG = "logback.xml";

	private static final String APPLICATION_SETTINGS_PROPERTIES = "settings.properties";

	private static final String APPLICATION_NAME_KEY = "application.name";
	private static final String WORKING_DIR_KEY = "working.dir";
	private static final String LOG_DIR_KEY = "log.dir";

	// for invoke init method
	private static boolean initialized = false;

	static String applicationName = null;

	private static final Object workingDirToken = new Object();
	static File workingDir = null;

	private static final Object logDirToken = new Object();
	static File logDir = null;

	static AbstractConfiguration configuration = null;

	static {
		init();
	}

	private ApplicationContext() {
		super();
	}

	public static boolean isInitialized() {
		return initialized;
	}

	public static String getApplicationName() {
		return applicationName;
	}

	public static AbstractConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * 
	 * @return the application workingDir
	 */
	public static File getWorkingDir() {

		if (null != workingDir) return workingDir;

		synchronized (workingDirToken) {

			if (null != workingDir) return workingDir;

			workingDir = readWorkingDir();
		}
		return workingDir;

	}

	/**
	 * 
	 * @return the application logDir
	 */
	public static File getLogDir() {

		if (null != logDir) return logDir;

		synchronized (logDirToken) {

			if (null != logDir) return logDir;

			logDir = readLogDir();
		}
		return logDir;

	}

	/**
	 * read the workingDir
	 * 
	 * @return
	 */
	protected static File readWorkingDir() {

		String workingPath = System.getProperty(WORKING_DIR_KEY);

		if ((null == workingPath) || (workingPath.isEmpty())) {
			workingPath = configuration.getString(WORKING_DIR_KEY);
		}
		File tempDir = null;
		if (null == workingPath) {
			tempDir = resolveBaseDir();
			if (null != tempDir) {
				tempDir = new File(tempDir, "business");
			}
		}
		else {
			tempDir = new File(workingPath);
		}

		if (null != tempDir) setWorkingDir(tempDir);

		return workingDir;
	}

	/**
	 * read the logDir
	 * 
	 * @return
	 */
	protected static File readLogDir() {

		String logPath = System.getProperty(LOG_DIR_KEY);
		if ((null == logPath) || (logPath.isEmpty())) {
			logPath = configuration.getString(LOG_DIR_KEY);
		}

		File tempDir = null;
		if (null == logPath) {
			tempDir = resolveBaseDir();
			if (null != tempDir) {
				tempDir = new File(tempDir, "log");
			}
		}
		else {
			tempDir = new File(logPath);
		}

		if (null != tempDir) setLogDir(tempDir);

		return logDir;
	}

	private static File resolveBaseDir() {
		File tempDir = ClassPathUtils.getResourceDir(LOG_CONFIG_TEST);
		if (null == tempDir)
			tempDir = ClassPathUtils.getResourceDir(LOG_CONFIG);
		if (null != tempDir) tempDir = tempDir.getParentFile();
		return tempDir;
	}

	/**
	 * set the new workingDir
	 * 
	 * @param newWorkingDir
	 */
	public static synchronized void setWorkingDir(File newWorkingDir) {

		workingDir = newWorkingDir;

		if (!workingDir.exists()) workingDir.mkdirs();

		String path = workingDir.getPath();
		configuration.setProperty(WORKING_DIR_KEY, path);
		System.setProperty(WORKING_DIR_KEY, path);

		System.out.println("Set application working.dir = ".concat(path));
	}

	/**
	 * set the new logDir
	 * 
	 * @param newLogDir
	 */
	public static synchronized void setLogDir(File newLogDir) {

		logDir = newLogDir;

		if (!logDir.exists()) logDir.mkdirs();

		String path = logDir.getPath();
		configuration.setProperty(LOG_DIR_KEY, path);
		System.setProperty(LOG_DIR_KEY, path);

		System.out.println("Set application log.dir = ".concat(path));
	}

	public synchronized static boolean init() {

		if (initialized) return true;

		try {

			System.out.println("Load application settings file '".concat(
					APPLICATION_SETTINGS_PROPERTIES).concat("'."));

			configuration = ConfigurationHelper
					.newConfigurationFromResource(APPLICATION_SETTINGS_PROPERTIES);
			configuration.setThrowExceptionOnMissing(false);

			System.out.println("Load application settings file: DONE.");

			applicationName = configuration.getString(
					APPLICATION_NAME_KEY, "UndefinedApplicationName");

			readWorkingDir();
			readLogDir();

		}
		catch (Throwable e) {
			System.out
					.println("Loading application settings file receive exception.");
			e.printStackTrace(System.out);
		}

		//configuration.setThrowExceptionOnMissing(true);
		// allow to throw the exception 'java.util.NoSuchElementException' when
		// a required property is missing

		initialized = true;
		return true;

	}

}
