package am.ajf.forge.util;

import java.io.File;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.MetadataFacet;

public class ProjectUtils {

	public static final String PROJECT_TYPE_PARENT = "parent";
	public static final String PROJECT_TYPE_EAR = "ear";
	public static final String PROJECT_TYPE_CORE = "core";
	public static final String PROJECT_TYPE_WS = "ws";
	public static final String PROJECT_TYPE_UI = "ui";
	public static final String PROJECT_TYPE_LIB = "lib";
	public static final String PROJECT_TYPE_CONFIG = "config";

	public static final String PROJECT_TYPE_COMPACT = "compacted";

	public static final String PROJECT_GROUPID_PREFIX = "am.projects";

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

	/**
	 * Return
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

		// Get the MavenFacet in order to grab the pom
		MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mavenCoreFacet.getPOM();

		// Create the dependecy
		Dependency dependency = new Dependency();
		dependency.setGroupId(projectGroupId(globalProjectName));
		dependency.setVersion("1.0.0-SNAPSHOT");
		dependency.setArtifactId(globalProjectName + "-"
				+ dependencyProjectType);

		// Add the dependency to the pom
		pom.addDependency(dependency);
		mavenCoreFacet.setPOM(pom);

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

		parent.setVersion("1.0.0-SNAPSHOT");
		parent.setRelativePath("../" + globalProjectName + "-"
				+ ProjectUtils.PROJECT_TYPE_PARENT + "/pom.xml");

		// Set the parent to the pom of the project
		pom.setParent(parent);
		mavenCoreFacet.setPOM(pom);
	}

}
