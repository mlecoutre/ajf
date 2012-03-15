package am.ajf.forge.plugin;

import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_COMPACT;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CONFIG;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CORE;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_EAR;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_LIB;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_PARENT;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_UI;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_WS;

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

//import org.jboss.forge.shell.plugins.builtin.NewProjectPackagingTypeCompleter;

/**
 * 
 * @author E019851
 * 
 */
@Alias("ajf-solution")
@Help("Create a new AJF solution in selected directory.")
public class CreateSolutionPlugin implements
		org.jboss.forge.shell.plugins.Plugin {

	@Inject
	private Shell shell;

	@Inject
	private ProjectFactory projectFactory;

	@Inject
	private ResourceFactory factory;

	private static final String AJF_PROJECT_TYPE_SIMPLE = "Simple ajf solution";
	private static final String AJF_PROJECT_TYPE_COMPLEX = "Complex ajf solution";

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

			String projectName = shell.prompt("Project Name :");
			String projectDirectory = promptProjectDirectory("Project directory (empty is current directory) :");
			if (!"exit".equals(projectDirectory))
				createAjfSolutionCompacted(projectName, projectDirectory, out);

		} else if (AJF_PROJECT_TYPE_COMPLEX.equals(projectType)) {

			String projectName = shell.prompt("Project Name :");
			String projectDirectory = promptProjectDirectory("Project directory (empty is current directory) :");
			if (!"exit".equals(projectDirectory))
				createAjfSolutionExploded(projectName, projectDirectory, out);

		} else {

			ShellMessages.error(out, "ERROR OCCURED");

		}

	}

	/**
	 * Return a correct directory for the generated project
	 * 
	 * @param shellPromptMessage
	 * @return projectDirectory
	 */
	private String promptProjectDirectory(String shellPromptMessage) {

		boolean isCorrectDirectory = false;
		String projectDirectory = null;

		while (isCorrectDirectory == false) {
			// Loop on project directory prompt message until directory is
			// correct (or 'exit')
			projectDirectory = shell.prompt(shellPromptMessage);
			isCorrectDirectory = checkProjectDirectoryConsistency(projectDirectory);

		}

		return projectDirectory;
	}

	/**
	 * Validate that the input directory (in which the project is to be
	 * generated) is a correct directory. If it is empty, the current shell
	 * directory is used. Elseway the directory is checked by an attempt of
	 * creating it. If the directory uses a wrong drives (or another error) this
	 * will be noticed.
	 * 
	 * @param projectDirectory
	 * @return
	 */
	private boolean checkProjectDirectoryConsistency(String projectDirectory) {

		ShellMessages.info(shell, "Checking project directory...");

		File myFile;
		boolean isCorrectDirectory = false;
		// If input project directory not set, use the current directory of
		// the shell
		if (null == projectDirectory || projectDirectory.isEmpty()) {

			projectDirectory = shell.getCurrentDirectory()
					.getUnderlyingResourceObject().getAbsolutePath();

		} else if ("exit".equals(projectDirectory)) {
			// way of escaping the loop
			return true;

		} else if (!projectDirectory.contains(":")) {
			// In case the hard drive is not specified, create a
			// subFolder in the current shell directory
			projectDirectory = shell.getCurrentDirectory()
					.getUnderlyingResourceObject().getAbsolutePath()
					.concat("/").concat(projectDirectory);

		}

		myFile = new File(projectDirectory);
		boolean isCreated = myFile.mkdir();
		if (!myFile.exists()) {
			if (!isCreated) {
				ShellMessages.error(shell,
						"Entered directory is not correct ! Please try Again");
				return false;
			}
		}

		List<String> options = new ArrayList<String>();
		options.add("Yes, let's do this !");
		options.add("No, change directory.");
		int choice = shell.promptChoice(
				"Are you sure to generate AJF project in :"
						.concat(projectDirectory), options);

		if (choice == 0) {
			// If choice "Yes" is selected
			isCorrectDirectory = true;
		}

		return isCorrectDirectory;
	}

	/**
	 * Creation of a 'complex ajf-solution' composed of different project types
	 * 
	 * @param name
	 * @param folderName
	 * @param out
	 */
	@Command("exploded")
	public void createAjfSolutionExploded(
			@Option(name = "named", description = "The name of the new AJF project", required = true) final String name,
			@Option(name = "Directory", required = true) final String folderName,
			final PipeOut out) {

		// Check project directory
		if (checkProjectDirectoryConsistency(folderName)) {

			/*
			 * START LOG
			 */
			ShellMessages.info(
					out,
					"Creating the AJF exploded solution".concat(name)
							.concat(" in the directory : ").concat(folderName));

			try {

				// Generate the list of different ajf project type
				generateAjfProject(name, folderName, PROJECT_TYPE_PARENT, out);

				generateAjfProject(name, folderName, PROJECT_TYPE_EAR, out);

				generateAjfProject(name, folderName, PROJECT_TYPE_CORE, out);

				generateAjfProject(name, folderName, PROJECT_TYPE_UI, out);

				generateAjfProject(name, folderName, PROJECT_TYPE_CONFIG, out);

				generateAjfProject(name, folderName, PROJECT_TYPE_WS, out);

				generateAjfProject(name, folderName, PROJECT_TYPE_LIB, out);

				/*
				 * FINAL LOG
				 */
				ShellMessages.info(out, "AJF solution done.[" + folderName
						+ "]");

			} catch (Exception e) {

				// print on the shell the exception thrown
				ShellMessages.error(out,
						"AJF project generation process has thrown an Exception : "
								+ e.toString());

			}
		} else {

			ShellMessages
					.error(shell,
							"Input project directory is not correct. Please try again...");

		}

	}

	/**
	 * Creation of an AJF compact project
	 * 
	 * @param name
	 * @param folderName
	 * @param out
	 */
	@Command("compacted")
	public void createAjfSolutionCompacted(
			@Option(name = "named", description = "The name of the new AJF project", required = true) final String name,
			@Option(name = "Directory", required = true) final String folderName,
			final PipeOut out) {

		/*
		 * START LOG
		 */
		ShellMessages.info(
				out,
				"Creating the AJF compacted solution".concat(name)
						.concat(" in the directory : ").concat(folderName));

		try {
			generateAjfProject(name, folderName, PROJECT_TYPE_COMPACT, out);
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

		// Begining of the Name of all the java packages of the project
		String javaPackage = "am." + globalProjectName.replace("-", ".");

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
