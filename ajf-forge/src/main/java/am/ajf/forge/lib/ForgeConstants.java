package am.ajf.forge.lib;

/**
 * Constants that are used in the AJF-FORGE engine. Mostly the path of the
 * resources are stored here in case they need to be re organized
 * 
 * @author E019851
 * 
 */
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
	public static final String PROJECT_TYPE_EJB = "ejb";
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
	 * Resources zip files
	 */
	public static final String WEBAPP_ZIP_RESOURCES = "webResources/WebAppResources.zip";
	public static final String WEBAPP_ZIP_RESOURCES_WS = "webResources/WebAppResources-ws.zip";
	public static final String UI_MAIN_RESOURCES = "webResources/UIResourcesMain.zip";
	public static final String UI_TEST_RESOURCES = "webResources/UIResourcesTest.zip";
	public static final String META_INF_FOLDER_ZIP = "META-INF.zip";
	public static final String PERSISTENCE_XML_ZIP = "persistence.zip";
	public static final String SITE_FOLDER = "site.zip";

	/*
	 * Resources xml file
	 */
	public static final String MODEL_POM_UI = "exploded-poms/pom-ui.xml";
	public static final String MODEL_POM_UI_COMPACT = "webResources/pom-ui-compact.xml";
	public static final String MODEL_POM_WS = "exploded-poms/pom-ws.xml";
	public static final String MODEL_POM_CONFIG = "exploded-poms/pom-config.xml";
	public static final String MODEL_POM_CORE = "exploded-poms/pom-core.xml";
	public static final String MODEL_POM_PARENT = "exploded-poms/pom-parent.xml";
	public static final String MODEL_POM_EAR = "exploded-poms/pom-ear.xml";
	public static final String MODEL_POM_LIB = "exploded-poms/pom-lib.xml";
	public static final String MODEL_POM_EJB = "exploded-poms/pom-ejb.xml";

	/*
	 * Velocity
	 */
	public static final String SITE_XML_VELOCITY_TEMPLATE = "site.xml.vm";
	// variable of the template:
	public static final String VELOCITY_VAR_APPLINAME = "appliName";
	public static final String VELOCITY_VAR_ISREPORT = "isReports";
	public static final String VELOCITY_VAR_ISPARENT = "isParentProject";

	/*
	 * FreeMarker
	 */
	public static final String DEFAULT_TEMPLATES_DIRECTORY = "/FreeMarkerTemplate";
	public static final String DEFAULT_TEMPLATES_DIRECTORY_ZIP = "FreeMarkerTemplate.zip";

}
