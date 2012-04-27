package am.ajf.forge.plugin;

import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_COMPACT;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CONFIG;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CORE;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_EAR;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_EJB;
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
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceException;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.util.Files;
import org.jboss.forge.shell.util.ResourceUtil;

import am.ajf.forge.core.CreateProject;
import am.ajf.forge.lib.ForgeConstants;

/**
 * 
 * @author E019851
 * 
 */
@Alias("ajf-solution")
@Help("Create a new AJF2 java solution")
public class CreateSolutionPlugin implements Plugin {

	@Inject
	private Shell shell;

	@Inject
	private ProjectFactory projectFactory;

	@Inject
	private ResourceFactory factory;

	boolean isProjectDirFlag = false;

	/**
	 * Default command of the plug-in which will prompt the user in order to let
	 * him chose which kind of AJF-Solutin he wants to generate
	 * 
	 * @param projectType
	 *            'c' for compacted ajf project and 'e' for exploded ajf
	 *            solution
	 * @param isWs
	 *            flag to generated the WS ajf component project (only for
	 *            exploded solution)
	 * @param isEjb
	 *            flag to generated the EJB ajf component project (only for
	 *            exploded solution)
	 * @param out
	 */
	@DefaultCommand
	public void createAjfSolution(
			@Option(name = "projectType", description = "C: Compacted web AJF project; E:Exploded AJF solution composed)", required = false) String projectType,
			@Option(name = "WS", description = "Set flag to generate WS component ajf project", flagOnly = true, required = false) boolean isWs,
			@Option(name = "EJB", description = "Set flag to generate EJB component ajf project", flagOnly = true, required = false) boolean isEjb,
			final PipeOut out) {

		String AJF_PROJECT_TYPE_SIMPLE = "Compacted ajf solution";
		String AJF_PROJECT_TYPE_COMPLEX = "Exploded ajf solution";

		/*
		 * Prompt choice for Wich AJF solution type to generate
		 */
		if (null == projectType) {

			List<String> options = new ArrayList<String>();

			options.add(AJF_PROJECT_TYPE_SIMPLE);
			options.add(AJF_PROJECT_TYPE_COMPLEX);

			int selectedoption = shell.promptChoice(
					"Which type of AJF project ?", options);

			projectType = options.get(selectedoption);

		}

		// The user could have entered "c" as project type for compacted
		// solution
		if (AJF_PROJECT_TYPE_SIMPLE.equals(projectType)
				|| "c".equals(projectType.toLowerCase())) {

			boolean doWeContinue = validateCompactedProject(isWs, isEjb, out);

			if (doWeContinue) {

				/*
				 * Case of generating a compact ajf solution
				 */
				String projectName = promptForProjectName();

				// the keyword "exit" is always used to exit the prompt loop
				if ("exit".equals(projectName)) {
					ShellMessages.info(out, "bye bye !");

				} else {
					String projectDirectory = promptProjectDirectory("Project directory (empty is current directory) :");
					if (!"exit".equals(projectDirectory)) {

						isProjectDirFlag = true;
						// Call the compact project generation method
						createAjfSolutionCompacted(projectName,
								projectDirectory, out);
					} else {
						ShellMessages.info(out, "bye bye !");
					}
				}

			}
			// The user could have entered "e" as project type for exploded
			// solution
		} else if (AJF_PROJECT_TYPE_COMPLEX.equals(projectType)
				|| "e".equals(projectType.toLowerCase())) {

			boolean doWeContinue = validateAjfProjectToUser(isWs, isEjb);

			if (doWeContinue) {

				/*
				 * Case of generating an exploded ajf solution
				 */
				String projectName = promptForProjectName();

				// Possibility to exit the prompt loop
				if ("exit".equals(projectName)) {
					ShellMessages.info(out, "bye bye !");
				} else {

					String projectDirectory = promptProjectDirectory("Project directory (empty is current directory) :");

					if (!"exit".equals(projectDirectory)) {

						isProjectDirFlag = true;

						// Call the exploded project generation method
						createAjfSolutionExploded(projectName,
								projectDirectory, isWs, isEjb, out);

					} else {
						ShellMessages.info(out, "bye bye !");
					}
				}
			}

		} else {

			ShellMessages.error(out, "Project Type ".concat(projectType)
					.concat(" does not exist... please try again..."));

		}

	}

	/**
	 * Generation of an exploded ajf solution
	 * 
	 * @param name
	 *            the project to generate
	 * @param folderName
	 *            where to generate project
	 * @param isWs
	 *            flag to generate the project-ws component
	 * @param isEjb
	 *            flag to generate the project-ejb component
	 * @param out
	 */
	@Command("exploded")
	public void createAjfSolutionExploded(
			@Option(name = "named", description = "The name of the new AJF project", required = true) final String name,
			@Option(name = "Directory", description = "Directory where to generate project", required = true) String folderName,
			@Option(name = "WS", description = "Optional: if a project-ws has to be generated", flagOnly = true, required = false) boolean isWs,
			@Option(name = "EJB", description = "Optional: if a project-ejb has to be generated", flagOnly = true, required = false) boolean isEjb,
			final PipeOut out) {

		// Check project directory
		if (isProjectDirFlag || checkProjectDirectoryConsistency(folderName)) {

			// let the user escape the prompt loop
			if ("exit".equals(folderName) || "exit".equals(name)) {
				ShellMessages.info(out, "bye bye !");

			} else {

				ShellMessages.info(
						out,
						"Creating the AJF exploded solution".concat(name)
								.concat(" in the directory : ")
								.concat(folderName));
				try {

					/*
					 * Generate the list of different ajf project type
					 */
					generateAjfProject(name, folderName, PROJECT_TYPE_PARENT,
							out);
					generateAjfProject(name, folderName, PROJECT_TYPE_EAR, out);
					generateAjfProject(name, folderName, PROJECT_TYPE_CORE, out);
					generateAjfProject(name, folderName, PROJECT_TYPE_UI, out);
					generateAjfProject(name, folderName, PROJECT_TYPE_CONFIG,
							out);
					generateAjfProject(name, folderName, PROJECT_TYPE_LIB, out);

					// Optional projects ws and ejb
					if (isWs)
						generateAjfProject(name, folderName, PROJECT_TYPE_WS,
								out);
					if (isEjb)
						generateAjfProject(name, folderName, PROJECT_TYPE_EJB,
								out);

					ShellMessages.info(out, "AJF solution done.[" + folderName
							+ "]");

				} catch (Exception e) {

					// print on the shell the exception thrown
					ShellMessages.error(out,
							"AJF project generation process has thrown an Exception : "
									+ e.toString());

				}
			}
		} else {

			// Do nothing - If coming here : problem with input project folder
		}
	}

	/**
	 * Creation of an AJF compact project (UI typed project)
	 * 
	 * @param name
	 *            of the generated project
	 * @param folderName
	 *            where to generate the project
	 * @param out
	 */
	@Command("compacted")
	public void createAjfSolutionCompacted(
			@Option(name = "named", description = "The name of the new AJF project", required = true) final String name,
			@Option(name = "Directory", description = "Directory where to generate project", required = true) String folderName,
			final PipeOut out) {

		// Check project directory
		if (isProjectDirFlag || checkProjectDirectoryConsistency(folderName)) {

			if ("exit".equals(folderName) || "exit".equals(name)) {
				ShellMessages.info(out, "bye bye !");
			} else {
				ShellMessages.info(
						out,
						"Creating the AJF compacted solution".concat(name)
								.concat(" in the directory : ")
								.concat(folderName));
				try {
					generateAjfProject(name, folderName, PROJECT_TYPE_COMPACT,
							out);

				} catch (Exception e) {
					// print on the shell the exception thrown
					ShellMessages.error(out,
							"AJF project generation process has thrown an Exception : "
									+ e.toString());
				}
			}
		} else {
			// Do nothing - If coming here : problem with input project folder
		}

	}

	/**
	 * In case of a compacted ajf solution. WS or EJB component must not be set.
	 * In case they are set, the user is prompted with warning, with possibity
	 * to stop the generation process
	 * 
	 * @param isWs
	 * @param isEjb
	 * @param out
	 * @return booelan true if we can process the generation
	 */
	private boolean validateCompactedProject(boolean isWs, boolean isEjb,
			final PipeOut out) {
		// flag for error
		boolean doWeContinue = true;

		// WS or AJB component can't be set for compacted project
		if (isEjb || isWs) {
			doWeContinue = shell
					.promptBoolean(
							"WARNING : flag for EJB or WS project generation will not take effect in a compacted ajf project. Do you wish to continue ?",
							true);
			if (!doWeContinue) {
				ShellMessages.info(out,
						"The ajf-solution generation will be stopped.");
			}
		}
		return doWeContinue;
	}

	/**
	 * Prompt the user for validation of wich of EJB or WS component will be
	 * generated whith his ajf solution
	 * 
	 * @param isWs
	 * @param isEjb
	 * @return
	 */
	private boolean validateAjfProjectToUser(boolean isWs, boolean isEjb) {
		boolean doWeContinue = true;

		if (isEjb && isWs) {

			doWeContinue = shell
					.promptBoolean(
							"You are about to create an exploded AJF solution with WS and EJB Component. Continue ?",
							true);
		} else {

			if (isEjb)
				doWeContinue = shell
						.promptBoolean(
								"You are about to create an exploded AJF solution with EJB Component. Continue ?",
								true);

			if (isWs)
				doWeContinue = shell
						.promptBoolean(
								"You are about to create an exploded AJF solution with WS Component. Continue ?",
								true);

		}
		return doWeContinue;
	}

	/**
	 * Generate the AJF solution corresponding to the input 'ProjectType'
	 * architecture
	 * 
	 * @param globalProjectName
	 *            name of the global solution
	 * @param projectFolder
	 *            where to create the ajf project
	 * @param projectType
	 *            type of the ajf project to create (i.e: 'ui')
	 * @param out
	 * @throws Exception
	 */
	private void generateAjfProject(final String globalProjectName,
			String projectFolder, String projectType, PipeOut out)
			throws Exception {

		// Setting the project name
		String projectFinalName;
		if (ForgeConstants.PROJECT_TYPE_COMPACT.equals(projectType)) {

			projectFinalName = globalProjectName + "-"
					+ ForgeConstants.PROJECT_TYPE_UI;
		} else {
			projectFinalName = globalProjectName + "-" + projectType;
		}

		// generate project complet path
		String projectCompletePath = projectFolder + "/" + projectFinalName;

		// Create the java file from the path
		File projectDirectoryFile = new File(projectCompletePath);

		// Create the Resource from the project file (needed for Forge api)
		Resource<?> projectResource = shell.getCurrentDirectory().createFrom(
				projectDirectoryFile);

		// Generated directory resource for project to be generated by forge api
		DirectoryResource dir = prepareDirectoryResource(projectFinalName,
				projectResource, out);

		// Java package that will be used to generate java class
		String javaPackage = "am." + globalProjectName.replace("-", ".");

		// Call the CreateProject class (out of the Plugin)
		CreateProject createProject = new CreateProject();
		createProject.createAjfProject(globalProjectName, javaPackage, dir,
				projectFactory, projectType, projectFinalName);

		ShellMessages.success(out, "Project : " + projectFinalName
				+ " created.");

		shell.println();

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
	 * Loop until user enter a project name.
	 * 
	 * @return projectName
	 */
	private String promptForProjectName() {
		String projectName = shell.prompt("Project Name :");
		while (null == projectName || projectName.isEmpty()) {
			projectName = shell.prompt("Project Name :");
		}
		return projectName;
	}

	/**
	 * Validate that the input directory (in which the project is to be
	 * generated) is a correct directory. If it is empty, the current shell
	 * directory is used. Elseway the directory is checked by an attempt of
	 * creating it. If the directory uses a wrong drives (or another error) this
	 * will be noticed.
	 * 
	 * @param projectDirectory
	 * @return boolean
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

		// Attempt of creating the project directory to see if an error occured
		myFile = new File(projectDirectory);
		boolean isCreated = myFile.mkdirs();
		if (!myFile.exists()) {
			if (!isCreated) {
				// if not correct, escape the loop and prompt error
				ShellMessages.error(shell,
						"Entered directory is not correct ! Please try Again");
				return false;
			}
		}

		// Ask user validation of the project directory
		List<String> options = new ArrayList<String>();
		options.add("Yes, let's do this !");
		options.add("No, change directory.");
		int choice = shell.promptChoice(
				"Are you sure to generate AJF project in :"
						.concat(projectDirectory), options);

		if (choice == 0) {
			// If choice "Yes" is selected
			isCorrectDirectory = true;
		} else {
			// If "No" choice selected, we delete the previously created project
			// directory
			if (myFile.exists()) {
				myFile.delete();
			}
			isCorrectDirectory = false;
		}

		return isCorrectDirectory;
	}

	/**
	 * Prepare Resource directory for project: this code is mainly duplicated of
	 * jboss forge "new-project" plugin source code. The aim of this is to
	 * prepare the directory resource for the project
	 * 
	 * @param projectname
	 * @param myProjectFolder
	 * @param out
	 * @return
	 */
	private DirectoryResource prepareDirectoryResource(
			final String projectname, Resource<?> projectFolder,
			final PipeOut out) {

		// This code is duplicated form jboss forge "new-project" plugin

		DirectoryResource dir = null;

		boolean skipFolderPrompt = false;
		try {
			if (projectFolder instanceof FileResource<?>) {
				if (!projectFolder.exists()) {
					((FileResource<?>) projectFolder).mkdirs();
					dir = projectFolder.reify(DirectoryResource.class);
					skipFolderPrompt = true;
				} else if (projectFolder instanceof DirectoryResource) {
					dir = (DirectoryResource) projectFolder;
					skipFolderPrompt = true;
				} else {
					ShellMessages.error(out,
							"File exists but is not a directory ["
									+ projectFolder.getFullyQualifiedName()
									+ "]");
				}
			}

			if (dir == null) {
				dir = shell.getCurrentDirectory()
						.getChildDirectory(projectname);
			}
		} catch (ResourceException e) {
		}

		if (!skipFolderPrompt
				&& (projectFactory.containsProject(dir) || !shell
						.promptBoolean("Use [" + dir.getFullyQualifiedName()
								+ "] as project directory?"))) {

			if (projectFactory.containsProject(dir)) {
				ShellMessages
						.error(out,
								"["
										+ dir.getFullyQualifiedName()
										+ "] already contains a project; please use a different folder.");
			}

			if (shell.getCurrentResource() == null) {
				dir = ResourceUtil.getContextDirectory(factory
						.getResourceFrom(Files.getWorkingDirectory()));
			} else {
				dir = shell.getCurrentDirectory();
			}

			FileResource<?> newDir;
			do {
				newDir = shell.getCurrentDirectory();
				shell.println();
				if (!projectFactory.containsProject(newDir
						.reify(DirectoryResource.class))) {
					newDir = shell
							.promptFile(
									"Where would you like to create the project? [Press ENTER to use the current directory: "
											+ newDir + "]", dir);
				} else {
					newDir = shell
							.promptFile("Where would you like to create the project?");
				}

				if (!newDir.exists()) {
					newDir.mkdirs();
					newDir = newDir.reify(DirectoryResource.class);
				} else if (newDir.isDirectory()
						&& !projectFactory.containsProject(newDir
								.reify(DirectoryResource.class))) {
					newDir = newDir.reify(DirectoryResource.class);
				} else {
					ShellMessages.error(
							out,
							"That folder already contains a project ["
									+ newDir.getFullyQualifiedName()
									+ "], please select a different location.");
					newDir = null;
				}

			} while ((newDir == null) || !(newDir instanceof DirectoryResource));

			dir = (DirectoryResource) newDir;
		}

		if (!dir.exists()) {
			dir.mkdirs();
		}

		System.out.println("** DEBUG  :" + dir.getFullyQualifiedName());
		return dir;
	}

}
