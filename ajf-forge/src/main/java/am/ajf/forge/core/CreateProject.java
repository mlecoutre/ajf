package am.ajf.forge.core;

import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_COMPACT;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CONFIG;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CORE;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_EAR;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_LIB;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_PARENT;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_UI;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_WS;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;

import am.ajf.forge.core.generators.ConfigProjectGenerator;
import am.ajf.forge.core.generators.CoreProjectGenerator;
import am.ajf.forge.core.generators.EarProjectGenerator;
import am.ajf.forge.core.generators.LibProjectGenerator;
import am.ajf.forge.core.generators.ParentProjectGenerator;
import am.ajf.forge.core.generators.WebProjectGenerator;
import am.ajf.forge.util.EclipseUtils;

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

			ParentProjectGenerator parentProjectgen = new ParentProjectGenerator();
			project = parentProjectgen.generateProjectParent(globalProjectName,
					javaPackage, projectFactory, projectFinalName, dir);

		} else if (PROJECT_TYPE_CONFIG.equals(projectType)) {

			ConfigProjectGenerator configProjectGenerator = new ConfigProjectGenerator();
			project = configProjectGenerator.generateProjectConfig(
					globalProjectName, javaPackage, projectFactory,
					projectFinalName, dir);

		} else if (PROJECT_TYPE_EAR.equals(projectType)) {

			EarProjectGenerator earProjectGenerator = new EarProjectGenerator();
			project = earProjectGenerator.generateProjectEar(globalProjectName,
					javaPackage, projectFactory, projectFinalName, dir);

		} else if (PROJECT_TYPE_CORE.equals(projectType)) {

			CoreProjectGenerator coreProjectGen = new CoreProjectGenerator();
			project = coreProjectGen.generateCoreAjfProject(globalProjectName,
					projectFinalName, javaPackage, projectFactory, projectType,
					dir);

		} else if (PROJECT_TYPE_UI.equals(projectType)) {

			WebProjectGenerator uiProject = new WebProjectGenerator();

			project = uiProject.generateUIAjfProject(globalProjectName,
					projectFinalName, javaPackage, projectFactory, projectType,
					dir, false);

		} else if (PROJECT_TYPE_LIB.equals(projectType)) {

			LibProjectGenerator libProjectGen = new LibProjectGenerator();
			project = libProjectGen.generateLibAjfProject(globalProjectName,
					projectFinalName, javaPackage, projectFactory, projectType,
					dir);

		} else if (PROJECT_TYPE_WS.equals(projectType)) {

			WebProjectGenerator uiProject = new WebProjectGenerator();
			project = uiProject.generateWSAjfProject(globalProjectName,
					projectFinalName, javaPackage, projectFactory, projectType,
					dir);

		} else if (PROJECT_TYPE_COMPACT.equals(projectType)) {

			WebProjectGenerator uiProject = new WebProjectGenerator();

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
		System.out.println("** DEBUG :".concat(globalProjectName + "-"
				+ projectType)
				+ " OK");

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
