package am.ajf.forge.lib;

public class ForgeConstants {

	/*
	 * Project types (used as project suffixes)
	 */
	public static final String PROJECT_TYPE_PARENT = "parent";
	public static final String PROJECT_TYPE_EAR = "ear";
	public static final String PROJECT_TYPE_CORE = "core";
	public static final String PROJECT_TYPE_WS = "ws";
	public static final String PROJECT_TYPE_UI = "ui";
	public static final String PROJECT_TYPE_LIB = "lib";
	public static final String PROJECT_TYPE_CONFIG = "config";
	public static final String PROJECT_TYPE_COMPACT = "compacted";

	/*
	 * GroupId prefix used for generated projects
	 */
	public static final String PROJECT_GROUPID_PREFIX = "am.projects";

	/*
	 * Web path used in web projects generated projects
	 */
	public static final String PROJECT_WEB_PATH = "/src/main/webapp";

	/*
	 * Initial milestone used as version of a new generated project
	 */
	public static final String START_PROJECT_MILESTONE = "1.0.0-SNAPSHOT";

	/*
	 * Standard parent
	 */
	public static final String STANDARD_PARENT_GROUPID = "am.parent";
	public static final String STANDARD_PARENT_ARTIFACTID = "standard";
	public static final String STANDARD_PARENT_VERSION = "2.0.7";

	/*
	 * Resources zip files
	 */
	public static final String WEBAPP_ZIP_RESOURCES = "WebAppResources.zip";
	public static final String UI_MAIN_RESOURCES = "UIResourcesMain.zip";
	public static final String UI_TEST_RESOURCES = "UIResourcesTest.zip";

	/*
	 * TOMCAT PLUGIN
	 */
	public static final String TOMCAT_PLUGIN_GROUPID = "org.apache.tomcat.maven";
	public static final String TOMCAT_PLUGIN_ARTIFACTID = "tomcat7-maven-plugin";
	public static final String TOMCAT_PLUGIN_VERSION = "2.0-beta-1";
	public static final String TOMCAT_DEPENDENCIES_FILE = "tomcatPlugin.xml";
	public static final String TOMCAT_PLUGIN_PROFILE_DEPENDENCIES_FILE = "tomcatProfile.xml";
	public static final String TOMCAT_PLUGIN_GENERATED_CONTEXTFILEPATH = "src/test/resources/tomcat_context.xml";

	/*
	 * WAS PLUGIN
	 */
	public static final String WAS_PROFILE_DEPENDENCIES_FILE = "wasProfile.xml";
}
