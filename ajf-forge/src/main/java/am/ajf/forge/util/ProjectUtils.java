package am.ajf.forge.util;

import static am.ajf.forge.lib.ForgeConstants.PROJECT_GROUPID_PREFIX;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_PARENT;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_WEB_PATH;
import static am.ajf.forge.lib.ForgeConstants.START_PROJECT_MILESTONE;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
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

		// TODO add repository to generated project's pom.xml?
		// deps.addRepository(KnownRepository.JBOSS_NEXUS);

		return deps;
	}

	/**
	 * Add a dependency in the pom.xml of the input project to the project (of
	 * the same global project) corresponding to the input dependencyProjectType
	 * 
	 * For example : If Global project name is ajf-project.
	 * 
	 * if input Project=ajf-project-core and dependencyProjectType="lib" then a
	 * dependency on ajf-project-lib will be added to the ajf-project-core's
	 * pom.xml
	 * 
	 * @param globalProjectName
	 *            : Name of the global project (without any suffixe)
	 * @param project
	 *            object of the current project beeing generated
	 * @param dependencyProjectType
	 *            : project type of the internal project dependency.
	 */
	public static void addInternalDependency(String globalProjectName,
			Project project, String dependencyProjectType) {

		String artifactId = globalProjectName + "-" + dependencyProjectType;

		addDependency(project, projectGroupId(globalProjectName), artifactId,
				START_PROJECT_MILESTONE);

	}

	/**
	 * 
	 * @param resourcePomFile
	 * @return
	 * @throws Exception
	 */
	public static Model getPomFromFile(String resourcePomFile) throws Exception {

		try {

			InputStream pomModelIs = ProjectUtils.class.getClassLoader()
					.getResourceAsStream(resourcePomFile);
			Model pom = new Model();
			pom = new MavenXpp3Reader().read(pomModelIs);
			return pom;

		} catch (Exception e) {

			String message = "Error occured while readim reource pom File :"
					.concat(resourcePomFile);

			System.out.println("** ERROR : ".concat(message));

			throw new Exception(message, e);

		}

		// if (myPomFile.exists())
		// myPomFile.delete();
		//
		// myPomFile = null;

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
	 * the globalProjectname-parent according to the ajf convention. This works
	 * for an exploded ajf project;
	 * 
	 * @param globalProjectName
	 * @param project
	 */
	public static void setInternalPomParent(String globalProjectName,
			Project project) {

		String parentGroupId = projectGroupId(globalProjectName);
		String parentArtifactId = globalProjectName + "-" + PROJECT_TYPE_PARENT;

		String parentVersion = START_PROJECT_MILESTONE;

		String parentRelativePath = "../".concat(globalProjectName).concat("-")
				.concat(PROJECT_TYPE_PARENT).concat("/pom.xml");

		setPomParentWithPath(parentGroupId, parentArtifactId, parentVersion,
				parentRelativePath, project);

	}

	/**
	 * 
	 * Set a pom parent to the input project from a parent's groupId, artifactId
	 * and version
	 * 
	 * @param parentGroupId
	 * @param parentArtifactId
	 * @param parentVersion
	 * @param project
	 */
	public static void setPomParent(String parentGroupId,
			String parentArtifactId, String parentVersion, Project project) {

		setPomParentWithPath(parentGroupId, parentArtifactId, parentVersion,
				null, project);

	}

	/**
	 * Set a pom parent to the input project from a parent's groupId, artifactId
	 * version and relativePath. If the relative path is set to 'null', the
	 * relativePath is not set for the parent
	 * 
	 * @param parentGroupId
	 * @param parentArtifactId
	 * @param parentVersion
	 * @param parentRelativPath
	 * @param project
	 */
	private static void setPomParentWithPath(String parentGroupId,
			String parentArtifactId, String parentVersion,
			String parentRelativPath, Project project) {

		// Get the Pom
		MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mavenCoreFacet.getPOM();

		// Create the parent
		Parent parent = new Parent();
		parent.setGroupId(parentGroupId);
		parent.setArtifactId(parentArtifactId);
		parent.setVersion(parentVersion);

		if (!(null == parentRelativPath))
			parent.setRelativePath(parentRelativPath);

		// Set the parent to the pom of the project
		pom.setParent(parent);
		mavenCoreFacet.setPOM(pom);

	}

	/**
	 * 
	 * Set all AJF dependencies to the input pom.xml, corresponding to input ajf
	 * artifact ids
	 * 
	 * @param ajfDependenciesArtifactIds
	 * @param sourceFileName
	 * @param pom
	 * @throws Exception
	 */
	public static void addAjfDependenciesToPom(
			List<String> ajfDependenciesArtifactIds, String sourceFileName,
			Model pom) throws Exception {

		try {
			InputStream mavenDepsIs = ProjectUtils.class.getClassLoader()
					.getResourceAsStream(sourceFileName);

			Model ajfDepsModel = new MavenXpp3Reader().read(mavenDepsIs);

			List<Dependency> deps = ajfDepsModel.getDependencies();

			for (String ajfDepName : ajfDependenciesArtifactIds) {
				for (Dependency dep : deps) {
					if (ajfDepName.equals(dep.getArtifactId())) {

						pom.addDependency(dep);
						break;

					}
				}
			}

		} catch (Exception e) {

			System.err
					.println("**ERROR : exception occured while setting AJF dependencies to project");
			throw e;

		}
	}
}
