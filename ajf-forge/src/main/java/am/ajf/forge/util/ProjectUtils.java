package am.ajf.forge.util;

import java.io.File;

import org.jboss.forge.project.Project;

public class ProjectUtils {

	public static final String PROJECT_TYPE_PARENT = "parent";
	public static final String PROJECT_TYPE_EAR = "ear";
	public static final String PROJECT_TYPE_CORE = "core";
	public static final String PROJECT_TYPE_WS = "ws";
	public static final String PROJECT_TYPE_UI = "ui";
	public static final String PROJECT_TYPE_LIB = "lib";
	public static final String PROJECT_TYPE_CONFIG = "config";

	/**
	 * Create an src/main/webapp/WEB-INF package for the input project
	 * 
	 * @param project
	 */
	public static void generateWebAppDirectory(Project project) {

		File webAppPackagePath = new File(project.getProjectRoot()
				.getUnderlyingResourceObject().getAbsolutePath()
				.concat("/src/main/webapp/WEB-INF"));

		if (!webAppPackagePath.exists()) {
			webAppPackagePath.mkdirs();
		}

	}

}
