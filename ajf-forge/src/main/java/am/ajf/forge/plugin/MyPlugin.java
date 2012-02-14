package am.ajf.forge.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;

import am.ajf.forge.core.CreateProject;
import am.ajf.forge.util.EclipseUtils;
import am.ajf.forge.util.ProjectUtils;

//import org.jboss.forge.shell.plugins.builtin.NewProjectPackagingTypeCompleter;

/**
 * 
 * @author E019851
 * 
 */
@Alias("ajf-solution")
@Help("Create a new AJF solution in selected directory.")
public class MyPlugin implements org.jboss.forge.shell.plugins.Plugin {

	@Inject
	private Shell shell;

	@Inject
	private ProjectFactory projectFactory;

	@Inject
	private ResourceFactory factory;

	private static final String AJF_PROJECT_TYPE_SIMPLE = "S";
	private static final String AJF_PROJECT_TYPE_COMPLEX = "C";

	/**
	 * Default command of the plug-in which will prompt the user in order to let
	 * him chose which kind of AJF-Solutin he wants to generate
	 * 
	 * @param projectType
	 * @param out
	 */
	@DefaultCommand
	public void createAjfSolution(
			@Option(name = "projectType", description = "S: Simple AJF project; C:Complex AJF project split in different projects (parent, ui, core...)", required = false) String projectType,
			final PipeOut out) {

		/*
		 * if user does not fill in the otion, he will be prompted of which ajf
		 */
		if (null == projectType) {

			List<String> options = new ArrayList<String>();

			options.add(AJF_PROJECT_TYPE_SIMPLE);
			options.add(AJF_PROJECT_TYPE_COMPLEX);

			int selectedoption = shell.promptChoice(
					"Which type of AJF project ?", options);

			projectType = options.get(selectedoption);

		}

		if (AJF_PROJECT_TYPE_SIMPLE.equals(projectType)) {

			ShellMessages.info(out, "SIPMPLE AJF solution generation to call");

		} else if (AJF_PROJECT_TYPE_COMPLEX.equals(projectType)) {

			ShellMessages.info(out, "COMPLEX AJF solution generation to call");

		} else {

			ShellMessages.error(out, "ERROR OCCURED");

		}

	}

	/**
	 * Creation of a 'complex ajf-solution' composed of different project types
	 * 
	 * @param name
	 * @param folderName
	 * @param out
	 */
	@Command("complex")
	public void createAjfSolutionComplex(
			@Option(name = "named", description = "The name of the new AJF project", required = true) final String name,
			@Option(name = "Directory", required = true) final String folderName,
			final PipeOut out) {

		/*
		 * START LOG
		 */
		ShellMessages.info(out, "Creating the AJF solution...");

		try {

			/*
			 * project parent
			 */
			generateAjfProject(name, folderName,
					ProjectUtils.PROJECT_TYPE_PARENT, out);

			/*
			 * project ear
			 */
			generateAjfProject(name, folderName, ProjectUtils.PROJECT_TYPE_EAR,
					out);

			/*
			 * project core
			 */
			generateAjfProject(name, folderName,
					ProjectUtils.PROJECT_TYPE_CORE, out);

			/*
			 * project ui
			 */
			generateAjfProject(name, folderName, ProjectUtils.PROJECT_TYPE_UI,
					out);

			/*
			 * project config
			 */
			generateAjfProject(name, folderName,
					ProjectUtils.PROJECT_TYPE_CONFIG, out);

			/*
			 * project WS
			 */
			generateAjfProject(name, folderName, ProjectUtils.PROJECT_TYPE_WS,
					out);

			/*
			 * project lib
			 */
			generateAjfProject(name, folderName, ProjectUtils.PROJECT_TYPE_LIB,
					out);

			/*
			 * FINAL LOG
			 */
			ShellMessages.info(out, "AJF solution done.[" + folderName + "]");

		} catch (Exception e) {

			// print on the shell the exception thrown
			ShellMessages.error(out,
					"AJF project generation process has thrown an Exception : "
							+ e.toString());

		}

	}

	/**
	 * generate the AJF solution corresponding to the input 'ProjectType'
	 * architecture
	 * 
	 * @param globalProjectName
	 * @param folderName
	 * @param projectType
	 * @param out
	 * @throws Exception
	 */
	private void generateAjfProject(final String globalProjectName,
			final String folderName, String projectType, PipeOut out)
			throws Exception {

		// Generate final project name : projectname-projectType (AJF norm)
		String projectFinalName = globalProjectName + "-" + projectType;

		// Generate the Full path of the project
		String projectCompletePath = folderName + "/" + projectFinalName;

		// Create the Project directory and resource
		File file = new File(projectCompletePath);
		Resource<?> projectFolder = factory.getResourceFrom(file);

		// Name of the java package
		String javaPackage = "ajf." + projectType;

		// Call the CreateProject class (out of the Plugin)
		CreateProject createProject = new CreateProject();

		createProject.createAjfProject(globalProjectName, javaPackage,
				projectFolder, projectFactory, projectType, projectFinalName);

		// Log : End of the project creation
		ShellMessages.info(out,
				"Project : " + projectFolder.getFullyQualifiedName()
						+ " created.");

	}
}
