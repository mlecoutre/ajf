package am.ajf.forge.core;

import static am.ajf.forge.lib.ForgeConstants.MODEL_POM_CONFIG;
import static am.ajf.forge.lib.ForgeConstants.MODEL_POM_EAR;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_COMPACT;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CONFIG;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CORE;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_EAR;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_LIB;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_PARENT;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_UI;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_WS;

import java.io.File;
import java.io.IOException;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;

import am.ajf.forge.util.EclipseUtils;
import am.ajf.forge.util.ProjectUtils;

public class CreateProject {

	/**
	 * Creation of an AJF project. This method generate a project in accordance
	 * to the AJF norm, depending on the project type set as input (core,
	 * config, ear, ui...)
	 * 
	 * @param globalProjectName
	 * @param javaPackage
	 * @param projectFolder
	 * @param projectFactory
	 * @param projectType
	 * @param projectFinalName
	 * @throws Exception
	 */
	public void createAjfProject(String globalProjectName, String javaPackage,
			DirectoryResource dir, ProjectFactory projectFactory,
			String projectType, String projectFinalName) throws Exception {

		System.out.println("START generating ajf project " + projectType);

		/*
		 * Creation of the project depending on the project type
		 */
		Project project = null;

		if (PROJECT_TYPE_PARENT.equals(projectType)) {

			ParentProjectGeneration parentProjectgen = new ParentProjectGeneration();
			project = parentProjectgen.generateProjectParent(globalProjectName,
					javaPackage, projectFactory, projectFinalName, dir);

		} else if (PROJECT_TYPE_CONFIG.equals(projectType)) {

			project = generateProjectConfig(globalProjectName, javaPackage,
					projectFactory, projectFinalName, dir);

		} else if (PROJECT_TYPE_EAR.equals(projectType)) {

			project = generateProjectEar(globalProjectName, javaPackage,
					projectFactory, projectFinalName, dir);

		} else if (PROJECT_TYPE_CORE.equals(projectType)) {

			CoreProjectGeneration coreProjectGen = new CoreProjectGeneration();
			project = coreProjectGen.generateCoreAjfProject(globalProjectName,
					projectFinalName, javaPackage, projectFactory, projectType,
					dir);

		} else if (PROJECT_TYPE_UI.equals(projectType)) {

			WebProjectGeneration uiProject = new WebProjectGeneration();

			project = uiProject.generateUIAjfProject(globalProjectName,
					projectFinalName, javaPackage, projectFactory, projectType,
					dir, false);

		} else if (PROJECT_TYPE_LIB.equals(projectType)) {

			LibProjectGeneration libProjectGen = new LibProjectGeneration();
			project = libProjectGen.generateLibAjfProject(globalProjectName,
					projectFinalName, javaPackage, projectFactory, projectType,
					dir);

		} else if (PROJECT_TYPE_WS.equals(projectType)) {

			WebProjectGeneration uiProject = new WebProjectGeneration();
			project = uiProject.generateWSAjfProject(globalProjectName,
					projectFinalName, javaPackage, projectFactory, projectType,
					dir);

		} else if (PROJECT_TYPE_COMPACT.equals(projectType)) {

			WebProjectGeneration uiProject = new WebProjectGeneration();

			project = uiProject.generateUIAjfProject(globalProjectName,
					projectFinalName, javaPackage, projectFactory, projectType,
					dir, true);

		} else {

			String errorMessage = "Unknown project type : ".concat(projectType);
			System.out.println(errorMessage);
			throw new Exception(errorMessage);

		}

		// Get the root directory of the current project
		String projectRootDirectory = project.getProjectRoot()
				.getFullyQualifiedName();

		/*
		 * generate classPath file
		 */
		EclipseUtils.generateClassPathFile(projectRootDirectory, projectType);

		/*
		 * End log
		 */
		System.out.println("Project".concat(globalProjectName + "-"
				+ projectType)
				+ " created");

	}

	/**
	 * Construction of an EAR core type project
	 * 
	 * @param globalProjectName
	 * @param javaPackage
	 * @param projectFactory
	 * @param projectFinalName
	 * @param dir
	 * @return Project object corresponding to the generated project
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Project generateProjectEar(String globalProjectName,
			String javaPackage, ProjectFactory projectFactory,
			String projectFinalName, DirectoryResource dir) throws Exception {
		Project project;

		project = projectFactory.createProject(dir, DependencyFacet.class,
				MetadataFacet.class, ResourceFacet.class);

		// Set pom from model
		ProjectUtils.setPomFromModelFile(project, MODEL_POM_EAR);

		ProjectUtils.setBasicProjectData(globalProjectName, projectFinalName,
				project);
		PackagingFacet packaging = project.getFacet(PackagingFacet.class);
		packaging.setPackagingType(PackagingType.JAR);

		/*
		 * Remove the Resource/Test folder of ear project
		 */
		String resourceTestPath = project.getFacet(ResourceFacet.class)
				.getTestResourceFolder().getParent().getFullyQualifiedName();
		File resourceTestFolder = new File(resourceTestPath);
		if (resourceTestFolder.exists()) {
			resourceTestFolder.delete();
		}

		/*
		 * Set the Pom parent
		 */
		ProjectUtils.setInternalPomParent(globalProjectName, project);

		/*
		 * Set dependencies
		 */
		ProjectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_WS);
		ProjectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_UI);
		ProjectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_CORE);
		ProjectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_LIB);
		ProjectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_CONFIG);

		return project;
	}

	/**
	 * Construction of an AJF Config type project
	 * 
	 * @param globalProjectName
	 * @param javaPackage
	 * @param projectFactory
	 * @param projectFinalName
	 * @param dir
	 * @return Project object corresponding to the generated project
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Project generateProjectConfig(String globalProjectName,
			String javaPackage, ProjectFactory projectFactory,
			String projectFinalName, DirectoryResource dir) throws Exception {

		Project project;
		System.out.println("Creating config project");

		project = projectFactory.createProject(dir, DependencyFacet.class,
				MetadataFacet.class, ResourceFacet.class);

		// Set pom from model
		ProjectUtils.setPomFromModelFile(project, MODEL_POM_CONFIG);

		ProjectUtils.setBasicProjectData(globalProjectName, projectFinalName,
				project);

		PackagingFacet packaging = project.getFacet(PackagingFacet.class);
		packaging.setPackagingType(PackagingType.JAR);

		String resourcesFolder = project.getFacet(ResourceFacet.class)
				.getResourceFolder().getFullyQualifiedName();

		System.out.println("--> Config resource path : " + resourcesFolder);

		// Create META-INF Folder
		File metainfFolder = new File(resourcesFolder + "/META-INF");
		metainfFolder.mkdir();

		/*
		 * Create Persistence.xml
		 */
		File persistenceFile = new File(metainfFolder.getAbsolutePath()
				+ "/persistence.xml");
		try {
			persistenceFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		persistenceFile = null;

		/*
		 * Create logback.xml
		 */
		File logbackFile = new File(resourcesFolder + "/logback.xml");

		try {
			logbackFile.createNewFile();
		} catch (IOException e) {
			System.out.println("** ERROR ");
		}
		logbackFile = null;

		/*
		 * Set the Pom parent
		 */

		ProjectUtils.setInternalPomParent(globalProjectName, project);
		return project;
	}

	// /**
	// * Initialization of the project directory
	// *
	// * @param projectFolder
	// * @return
	// * @throws Exception
	// */
	// private DirectoryResource initializeProjectDirectory(
	// Resource<?> projectFolder) throws Exception {
	// // Check Project Directory
	// DirectoryResource dir = null;
	// if (projectFolder instanceof FileResource<?>) {
	//
	// if (!projectFolder.exists()) {
	//
	// // Creation of folder
	// ((FileResource<?>) projectFolder).mkdirs();
	// dir = projectFolder.reify(DirectoryResource.class);
	//
	// } else if (projectFolder instanceof DirectoryResource) {
	// dir = (DirectoryResource) projectFolder;
	//
	// } else {
	// String errorMessage =
	// "The root directory of the project beeing generated is incorrect";
	// System.out.println(errorMessage);
	// throw new Exception(errorMessage);
	// }
	// }
	//
	// // Create folder
	// if (!dir.exists()) {
	// dir.mkdirs();
	// }
	// return dir;
	// }

}
