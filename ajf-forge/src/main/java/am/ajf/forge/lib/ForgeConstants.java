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
	public static final String WEBAPP_ZIP_RESOURCES = "webResources/WebAppResources.zip";
	public static final String UI_MAIN_RESOURCES = "webResources/UIResourcesMain.zip";
	public static final String UI_TEST_RESOURCES = "webResources/UIResourcesTest.zip";
	public static final String META_INF_FOLDER_ZIP = "META-INF.zip";

	/*
	 * Resources xml file
	 */
	public static final String UI_MODEL_POM_FILE = "webResources/pom-ui.xml";
	public static final String AJF_DEPS_MODEL_FILE = "ajf-dependencies.xml";
	public static final String EMPTY_BEANS_SOURCE_FILE = "beans-empty.xml";

	/*
	 * AJF ArtifactIds
	 */
	public static final String AJF_CORE = "ajf-core";
	public static final String AJF_INJECTION = "ajf-injection";
	public static final String AJF_MONITORING = "ajf-monitoring";
	public static final String AJF_NOTIF = "ajf-notif";
	public static final String AJF_PERSISTENCE = "ajf-persistence";
	public static final String AJF_REMOTING = "ajf-remoting";
	public static final String AJF_SECURITY = "ajf-SECURITY";
	public static final String AJF_TESTING = "ajf-testing";
	public static final String AJF_WEB = "ajf-web";

	/*
	 * FreeMarker
	 */
	public static final String DEFAULT_TEMPLATES_DIRECTORY = "/FreeMarkerTemplate";

}
