package ajf;

import java.io.File;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;

import ajf.configuration.ConfigurationFactory;
import ajf.logger.LoggerFactory;

public class ApplicationContext {

	private static final String APPLICATION_SETTINGS_PROPERTIES = "settings.properties";
	private static final String APPLICATION_NAME_KEY = "application.name";
	
	private static final String SYSTEM_WORKING_DIR_ATTRIBUTE = "working.dir";
	private static final String APPLICATION_WORKING_DIR_KEY = "application.workingDir";
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationContext.class);
		
	public static String applicationStage = null;
	public static String applicationName = null;
	
	private static final Object workingDirToken = new Object();
	public static File workingDir = null;
	
	public static CompositeConfiguration configuration = new CompositeConfiguration();
	public static Configuration applicationSettings = null;
	
	static {
		init();
	}
	
	private ApplicationContext() {
		super();
	}

	public static String getApplicationName() {
		return applicationName;
	}

	public static Configuration getConfiguration() {
		return configuration;
	}
	
	public static Configuration addConfiguration(Configuration newConfiguration) {
		configuration.addConfiguration(newConfiguration);
		return configuration;
	}
	
	/**
	 * 
	 * @return the application workingDir
	 */
	public static File getWorkingDir() {
		
		if (null != workingDir)
			return workingDir;
		
		synchronized (workingDirToken) {
			
			if (null != workingDir)
				return workingDir;
			
			workingDir = readWorkingDir();
		}
		return workingDir;
				
	}

	private static File readWorkingDir() {
		
		String workingPath = System.getProperty(SYSTEM_WORKING_DIR_ATTRIBUTE);
		if ((null == workingPath) || (workingPath.isEmpty())) {
			workingPath = configuration.getString(APPLICATION_WORKING_DIR_KEY);
		}
		
		File tempDir = new File(workingPath);
		setWorkingDir(tempDir);
		return tempDir;
	}	
	
	public static synchronized void setWorkingDir(File newWorkingDir) {
		workingDir = newWorkingDir;
		
		if (!workingDir.exists())
			workingDir.mkdirs();
			
		String path = workingDir.getPath();
		applicationSettings.setProperty(APPLICATION_WORKING_DIR_KEY, path);
		logger.info("Set application workingDir to: ".concat(path));
	}
	
	public static void init() {
		try {
			logger.info("Load application settings file '".concat(APPLICATION_SETTINGS_PROPERTIES).concat("'."));
			applicationSettings = ConfigurationFactory.getConfigurationFromResource(APPLICATION_SETTINGS_PROPERTIES);
			configuration.addConfiguration(applicationSettings);
			logger.info("Load application settings file: DONE.");
			
			applicationName = configuration.getString(APPLICATION_NAME_KEY, "UndefinedApplicationName");

			readWorkingDir();
			
		} catch (Throwable e) {
			logger.error("Loading application settings file receive exception.", e);
		}
	}
	
}
