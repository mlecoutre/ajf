package am.ajf.forge.helpers;

import static am.ajf.forge.lib.ForgeConstants.PROJECT_GROUPID_PREFIX;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_PARENT;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_UI;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_WS;
import static am.ajf.forge.lib.ForgeConstants.SITE_XML_VELOCITY_TEMPLATE;
import static am.ajf.forge.lib.ForgeConstants.START_PROJECT_MILESTONE;
import static am.ajf.forge.lib.ForgeConstants.VELOCITY_VAR_APPLINAME;
import static am.ajf.forge.lib.ForgeConstants.VELOCITY_VAR_ISPARENT;
import static am.ajf.forge.lib.ForgeConstants.VELOCITY_VAR_ISREPORT;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.inject.Singleton;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.velocity.VelocityContext;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.PipeOut;

/**
 * This utility class implements some methods that deals with the project
 * definition. Such as maven pom manipulation
 * 
 * @author E019851
 * 
 */

@Singleton
public class ProjectHelper {

	/**
	 * Return the group ID of the procject. Project group id, suffixed by the
	 * project global name
	 * 
	 * @param globalProjectName
	 * @return
	 */
	public String projectGroupId(String globalProjectName) {

		return PROJECT_GROUPID_PREFIX.concat("." + globalProjectName);

	}

	/**
	 * Uses example resources pom files to set the generated project's pom file.
	 * This method is particular for a UI ajf project as the 'isCompaced'
	 * boolean is waited as input. Only a UI project can be compacted.
	 * 
	 * @param project
	 *            current forge project
	 * @param modelPomFile
	 *            path of the resource example pom file
	 * @param isCompacted
	 *            true if the project is a compacted ajf project(can only be
	 *            true for a UI project)
	 * @throws Exception
	 */
	public void setPomFromModelFile(Project project, String modelPomFile)
			throws Exception {

		System.out.println("** START - generating pom.xml from model...");

		// Model Pom file
		Model modelPom = getPomFromFile(modelPomFile);

		// Get the current generated project's pom file
		MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
		Model generatedPom = mavenCoreFacet.getPOM();

		generatedPom = modelPom.clone();

		// set version
		generatedPom.setVersion(START_PROJECT_MILESTONE);

		mavenCoreFacet.setPOM(generatedPom);

		System.out.println("** END - pom.xml generated...");
	}

	/**
	 * Set basic project meta data : project group Id and Artifact Id
	 * 
	 * @param finalProjectName
	 * @param javaPackage
	 * @param project
	 * @return
	 */
	public void setBasicProjectData(String globalProjectName,
			String finalProjectName, Project project) {

		MetadataFacet meta = project.getFacet(MetadataFacet.class);

		// group-ID
		meta.setTopLevelPackage(projectGroupId(globalProjectName));

		// Artifact ID
		meta.setProjectName(finalProjectName);

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
	public void addInternalDependency(String globalProjectName,
			Project project, String dependencyProjectType) {

		addInternalDependencyScoped(globalProjectName, project,
				dependencyProjectType, null);

	}

	/**
	 * Aims to add an inter dependence to the current ajf exploded project. The
	 * possibility to set a scope is offered. Set scope to null if you don't
	 * want to set any scope to the dependendy
	 * 
	 * @param globalProjectName
	 * @param project
	 * @param dependencyProjectType
	 * @param scope
	 */
	public void addInternalDependencyScoped(String globalProjectName,
			Project project, String dependencyProjectType, String scope) {

		String artifactId = globalProjectName + "-" + dependencyProjectType;

		if (PROJECT_TYPE_UI.equals(dependencyProjectType)
				|| PROJECT_TYPE_WS.equals(dependencyProjectType)) {
			// In case it is internal dependency to UI or WS project, have to
			// set the "war" type
			addDependency(project, projectGroupId(globalProjectName),
					artifactId, START_PROJECT_MILESTONE, scope, "war");
		} else {
			addDependencyWithScope(project, projectGroupId(globalProjectName),
					artifactId, START_PROJECT_MILESTONE, scope);

		}

	}

	/**
	 * 
	 * Return the pom object corresponing to the input resource model pom file
	 * 
	 * @param resourcePomFile
	 *            path of the resource file corresponding to a pom.xml file
	 * @return Pom model object
	 * @throws Exception
	 */
	public Model getPomFromFile(String resourcePomFile) throws Exception {

		try {

			InputStream pomModelIs = ProjectHelper.class.getClassLoader()
					.getResourceAsStream(resourcePomFile);
			Model pom = new Model();
			pom = new MavenXpp3Reader().read(pomModelIs);
			return pom;

		} catch (Exception e) {

			String message = "Error occured while reading resource pom File :"
					.concat(resourcePomFile);

			System.out.println("** ERROR : ".concat(message));

			throw new Exception(message, e);

		}

	}

	/**
	 * Add a custom dependency to a project. This method will add to the input
	 * project's pom.xml the given maven dependency
	 * 
	 * @param project
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @param scope
	 * @param type
	 */
	public void addDependency(Project project, String groupId,
			String artifactId, String version, String scope, String type) {

		// Get the MavenFacet in order to grab the pom
		MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mavenCoreFacet.getPOM();

		// Create the dependecy
		Dependency dependency = new Dependency();
		dependency.setGroupId(groupId);
		dependency.setVersion(version);
		dependency.setArtifactId(artifactId);

		if (null != type) {
			dependency.setType(type);
		}

		if (null != scope) {
			dependency.setScope(scope);
		}

		// Add the dependency to the pom
		pom.addDependency(dependency);
		mavenCoreFacet.setPOM(pom);

	}

	/**
	 * Add a dependency to a project with the possibility to set to scope to it
	 * 
	 * @param project
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @param scope
	 */
	public void addDependencyWithScope(Project project, String groupId,
			String artifactId, String version, String scope) {

		addDependency(project, groupId, artifactId, version, scope, null);

	}

	/**
	 * Add a simple dependency to a project
	 * 
	 * @param project
	 * @param groupId
	 * @param artifactId
	 * @param version
	 */
	public void addSimpleDependency(Project project, String groupId,
			String artifactId, String version) {

		addDependency(project, groupId, artifactId, version, null, null);

	}

	/**
	 * Add a dependency management to the pom.xml of the input project
	 * 
	 * @param project
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @param type
	 */
	public void addManagementDependency(Project project, String groupId,
			String artifactId, String version, String type) {

		// Get the MavenFacet in order to grab the pom
		MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mavenCoreFacet.getPOM();

		// Create the dependecy
		Dependency dependency = new Dependency();
		dependency.setGroupId(groupId);
		dependency.setArtifactId(artifactId);
		dependency.setVersion(version);

		if (null != type && !type.isEmpty())
			dependency.setType(type);

		pom.getDependencyManagement().addDependency(dependency);

	}

	/**
	 * Add a property to the pom.xml file of the project
	 * 
	 * @param project
	 * @param propertyName
	 * @param propertyValue
	 */
	public void addPomProperty(Project project, String propertyName,
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
	public void setInternalPomParent(String globalProjectName, Project project) {

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
	public void setPomParent(String parentGroupId, String parentArtifactId,
			String parentVersion, Project project) {

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
	private void setPomParentWithPath(String parentGroupId,
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
	public void addAjfDependenciesToPom(
			List<String> ajfDependenciesArtifactIds, String sourceFileName,
			Model pom) throws Exception {

		try {
			InputStream mavenDepsIs = ProjectHelper.class.getClassLoader()
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

	/**
	 * Generates a site.xml file in the /src/site folder of the input project.
	 * Boolean isReports and isParent inputs, allows to make some special part
	 * of the site.xml template, depending on the project type
	 * 
	 * @param project
	 * @param applicationName
	 *            general application name (without ajf suffixe such as ui,
	 *            ws...)
	 * @param isReports
	 *            true if the 'Report' part of the site.xml file need to appear
	 * @param isParentProject
	 *            flag in case of a parent project, a dynamic part of the
	 *            site.xml (different menus) has to be created
	 * @throws Exception
	 */
	public void generateSiteXmlFile(Project project, String applicationName,
			boolean isReports, boolean isParentProject) throws Exception {

		// Site.xml file
		File siteXmlFile = new File(project.getProjectRoot()
				.getUnderlyingResourceObject().getAbsolutePath()
				.concat("/src/site/site.xml"));

		System.out.println("**DEBUG : siteXmlFile = "
				+ siteXmlFile.getAbsolutePath());

		// use velocity to generate file
		VelocityBuilder velocityBuilder = new VelocityBuilder();
		VelocityContext context = velocityBuilder.getContext();

		context.put(VELOCITY_VAR_APPLINAME, applicationName);

		// Allow to makes some part of the template file appear or not,
		// dependending on input booleans
		context.put(VELOCITY_VAR_ISREPORT, isReports);
		context.put(VELOCITY_VAR_ISPARENT, isParentProject);

		velocityBuilder.merge(SITE_XML_VELOCITY_TEMPLATE,
				siteXmlFile.getAbsolutePath());

	}

	/**
	 * Retrieve the project (as a forge Project object) contained in the same
	 * exploded ajf solution than the input project.
	 * 
	 * @param project
	 *            intial forge project from the global ajf solution
	 * @param projectFactory
	 * @param resourceFactory
	 * @param initialProjectType
	 *            ajf project suffixe of the initial Project set as input
	 * @param newProjectType
	 *            ajf project suffixe that you want to retrieve from the
	 *            solution. (i.e: "lib" if you want to retrive the
	 *            "projectName"-lib) * @param out
	 * @return Project
	 * @throws Exception
	 */
	public Project locateProjectFromSolution(Project project,
			ProjectFactory projectFactory, ResourceFactory resourceFactory,
			String initialProjectType, String newProjectType, PipeOut out)
			throws Exception {
		/*
		 * Locate the LIB project from the expoded AJF Solution
		 */
		File projectFile = project.getProjectRoot()
				.getUnderlyingResourceObject();

		File newProjectFile = new File(projectFile.getParent().concat("/")
				.concat(projectFile.getName())
				.replace(initialProjectType, newProjectType));

		// Check if the lib project directory does exist
		if (!newProjectFile.exists()) {
			throw new Exception(
					"The project "
							+ newProjectFile.getName()
							+ " does not exist. Please check that you are in an exploded ajf solution");
		}

		// Load the new project in the Project forge object
		Project newProject = projectFactory
				.findProject((DirectoryResource) resourceFactory
						.getResourceFrom(newProjectFile));

		projectFile = null;

		out.println();
		ShellMessages.info(out, "Project "
				+ newProjectFile.getName()
				+ " of your AJF solution has been loaded :"
				+ newProject.getProjectRoot().getUnderlyingResourceObject()
						.getAbsolutePath());
		newProjectFile = null;

		return newProject;
	}
}
