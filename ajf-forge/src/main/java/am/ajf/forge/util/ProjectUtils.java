package am.ajf.forge.util;

import java.io.File;
import java.util.Properties;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.MetadataFacet;

/**
 * This utility class implements some methods that deals with the project
 * definition. Such as maven pom manipulation etc.
 * 
 * @author E019851
 * 
 */
public class ProjectUtils {

	/*
	 * Public variables
	 */
	public static final String PROJECT_TYPE_PARENT = "parent";
	public static final String PROJECT_TYPE_EAR = "ear";
	public static final String PROJECT_TYPE_CORE = "core";
	public static final String PROJECT_TYPE_WS = "ws";
	public static final String PROJECT_TYPE_UI = "ui";
	public static final String PROJECT_TYPE_LIB = "lib";
	public static final String PROJECT_TYPE_CONFIG = "config";
	public static final String PROJECT_TYPE_COMPACT = "compacted";
	public static final String PROJECT_GROUPID_PREFIX = "am.projects";

	/*
	 * private variable
	 */
	private static final String PROJECT_WEB_PATH = "/src/main/webapp";
	private static final String START_PROJECT_MILESTONE = "1.0.0-SNAPSHOT";

	/**
	 * Create an src/main/webapp/WEB-INF package for the input project
	 * 
	 * 
	 * @param project
	 * @return File corresponding to webApp
	 */
	public static File generateWebAppDirectory(Project project) {

		System.out.println("** START - Generate webapp directory");

		File webInfPackagePath = new File(project.getProjectRoot()
				.getUnderlyingResourceObject().getAbsolutePath()
				.concat(PROJECT_WEB_PATH));

		if (!webInfPackagePath.exists()) {
			webInfPackagePath.mkdirs();
		}

		System.out.println("** END - Generate webapp directory");

		return webInfPackagePath;

	}

	/**
	 * Return the group ID of the procject. Project group id, suffixed by the
	 * project global name
	 * 
	 * @param globalProjectName
	 * @return
	 */
	public static String projectGroupId(String globalProjectName) {

		return PROJECT_GROUPID_PREFIX.concat("." + globalProjectName);

	}

	/**
	 * Set basic project meta data : project name and repository dependency
	 * 
	 * @param finalProjectName
	 * @param javaPackage
	 * @param project
	 * @return
	 */
	public static DependencyFacet setBasicProjectData(String globalProjectName,
			String finalProjectName, Project project) {

		// Set metadata
		MetadataFacet meta = project.getFacet(MetadataFacet.class);

		// group-ID
		meta.setTopLevelPackage(projectGroupId(globalProjectName));

		// Artifact ID
		meta.setProjectName(finalProjectName);

		// Dependencies
		DependencyFacet deps = project.getFacet(DependencyFacet.class);
		// TODO add repository
		// deps.addRepository(KnownRepository.JBOSS_NEXUS);

		return deps;
	}

	/**
	 * Add a dependency in the pom.xml of the input project to the project (of
	 * the same global project) corresponding to the input dependencyProjectType
	 * 
	 * For example : Global project ajf-project
	 * 
	 * if input Project=ajf-project-core and dependencyProjectType="lib" then a
	 * dependency on ajf-project-lib will be added to the ajf-project-core's
	 * pom.xml
	 * 
	 * @param globalProjectName
	 * @param project
	 *            object of the current project beeing generated
	 * @param dependencyProjectType
	 */
	public static void addInternalDependency(String globalProjectName,
			Project project, String dependencyProjectType) {

		String artifactId = globalProjectName + "-" + dependencyProjectType;

		addDependency(project, projectGroupId(globalProjectName), artifactId,
				START_PROJECT_MILESTONE);

	}

	/**
	 * Add a custom dependency to a project. This method will add to the input
	 * project's pom.xml the given maven dependency
	 * 
	 * @param project
	 * @param groupId
	 * @param artifactId
	 * @param version
	 */
	public static void addDependency(Project project, String groupId,
			String artifactId, String version) {

		// Get the MavenFacet in order to grab the pom
		MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mavenCoreFacet.getPOM();

		// Create the dependecy
		Dependency dependency = new Dependency();
		dependency.setGroupId(groupId);
		dependency.setVersion(version);
		dependency.setArtifactId(artifactId);

		// Add the dependency to the pom
		pom.addDependency(dependency);
		mavenCoreFacet.setPOM(pom);

	}

	/**
	 * Add a property to the pom.xml file of the project
	 * 
	 * @param project
	 * @param propertyName
	 * @param propertyValue
	 */
	public static void addPomProperty(Project project, String propertyName,
			String propertyValue) {

		MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mavenCoreFacet.getPOM();

		Properties properties = new Properties();
		properties.setProperty(propertyName, propertyValue);
		pom.setProperties(properties);

		mavenCoreFacet.setPOM(pom);

	}

	
	/**
	 * Insert in the pom.xml of the input project, the parent corresponding to
	 * the globalProjectname-parent according to the ajf convention
	 * 
	 * @param globalProjectName
	 * @param project
	 */
	public static void setPomParent(String globalProjectName, Project project) {

		// Get the Pom
		MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mavenCoreFacet.getPOM();

		// Create the parent
		Parent parent = new Parent();
		parent.setGroupId(projectGroupId(globalProjectName));
		parent.setArtifactId(globalProjectName + "-"
				+ ProjectUtils.PROJECT_TYPE_PARENT);

		parent.setVersion(START_PROJECT_MILESTONE);
		parent.setRelativePath("../" + globalProjectName + "-"
				+ ProjectUtils.PROJECT_TYPE_PARENT + "/pom.xml");

		// Set the parent to the pom of the project
		pom.setParent(parent);
		mavenCoreFacet.setPOM(pom);
	}

}
