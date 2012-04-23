package am.ajf.forge.plugin;

import static am.ajf.forge.lib.ForgeConstants.*;
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
@Help("Create a new AJF solution in selected directory.")
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
	 * @param out
	 */

	@DefaultCommand
	public void createAjfSolution(
			@Option(name = "projectType", description = "S: Simple AJF project; C:Complex AJF project split in different projects (parent, ui, core...)", required = false) String projectType,
			final PipeOut out) {

		String AJF_PROJECT_TYPE_SIMPLE = "Compacted ajf solution";
		String AJF_PROJECT_TYPE_COMPLEX = "Exploded ajf solution";

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

			String projectName = promptForProjectName();

			if ("exit".equals(projectName)) {
				ShellMessages.info(out, "bye bye !");
			} else {
				String projectDirectory = promptProjectDirectory("Project directory (empty is current directory) :");
				if (!"exit".equals(projectDirectory)) {
					isProjectDirFlag = true;
					createAjfSolutionCompacted(projectName, projectDirectory,
							out);
				} else {
					ShellMessages.info(out, "bye bye !");
				}
			}

		} else if (AJF_PROJECT_TYPE_COMPLEX.equals(projectType)) {

			String projectName = promptForProjectName();

			if ("exit".equals(projectName)) {
				ShellMessages.info(out, "bye bye !");
			} else {
				String projectDirectory = promptProjectDirectory("Project directory (empty is current directory) :");
				if (!"exit".equals(projectDirectory)) {
					isProjectDirFlag = true;
					createAjfSolutionExploded(projectName, projectDirectory,
							out);
				} else {
					ShellMessages.info(out, "bye bye !");
				}
			}

		} else {

			ShellMessages.error(out, "Project Type ".concat(projectType)
					.concat(" does not exist... please try again..."));

		}

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
			@Option(name = "Directory", required = true) String folderName,
			final PipeOut out) {

		// Check project directory
		if (isProjectDirFlag || checkProjectDirectoryConsistency(folderName)) {

			if ("exit".equals(folderName) || "exit".equals(name)) {
				ShellMessages.info(out, "bye bye !");
			} else {

				/*
				 * START LOG
				 */
				ShellMessages.info(
						out,
						"Creating the AJF exploded solution".concat(name)
								.concat(" in the directory : ")
								.concat(folderName));

				try {

					// Generate the list of different ajf project type
					generateAjfProject(name, folderName, PROJECT_TYPE_PARENT,
							out);
					generateAjfProject(name, folderName, PROJECT_TYPE_EAR, out);
					generateAjfProject(name, folderName, PROJECT_TYPE_CORE, out);
					generateAjfProject(name, folderName, PROJECT_TYPE_UI, out);
					generateAjfProject(name, folderName, PROJECT_TYPE_CONFIG,
							out);
					generateAjfProject(name, folderName, PROJECT_TYPE_LIB, out);

					// TODO:optional
					generateAjfProject(name, folderName, PROJECT_TYPE_WS, out);
					generateAjfProject(name, folderName, PROJECT_TYPE_EJB, out);

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
			}
		} else {

			// Do nothing - If coming here : problem with input project folder
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
			@Option(name = "Directory", required = true) String folderName,
			final PipeOut out) {

		// Check project directory
		if (isProjectDirFlag || checkProjectDirectoryConsistency(folderName)) {

			if ("exit".equals(folderName) || "exit".equals(name)) {
				ShellMessages.info(out, "bye bye !");
			} else {

				/*
				 * START LOG
				 */
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

		String projectCompletePath = projectFolder + "/" + projectFinalName;
		File file = new File(projectCompletePath);

		Resource<?> projectResource = shell.getCurrentDirectory().createFrom(
				file);
		DirectoryResource dir = prepareDirectoryResource(projectFinalName,
				projectResource, out);

		String javaPackage = "am." + globalProjectName.replace("-", ".");

		// Call the CreateProject class (out of the Plugin)
		CreateProject createProject = new CreateProject();
		createProject.createAjfProject(globalProjectName, javaPackage, dir,
				projectFactory, projectType, projectFinalName);

		/*
		 * Final Log
		 */

		ShellMessages.success(out, "Project : " + projectFinalName
				+ " created.");

		shell.println();

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
	 * 
	 * @return
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

		myFile = new File(projectDirectory);
		boolean isCreated = myFile.mkdirs();
		if (!myFile.exists()) {
			if (!isCreated) {
				ShellMessages.error(shell,
						"Entered directory is not correct ! Please try Again");
				return false;
			}
		}

		/*
		 * Ask user validation of the project directory
		 */
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
			if (myFile.exists()) {
				myFile.delete();
			}
		}

		return isCorrectDirectory;
	}

	/**
	 * Temporary command to be completed. Generate project resources without
	 * using forge-shell-api (not finished yet)
	 * 
	 * @param name
	 * @param projectFolder
	 * @param out
	 */
	// @Command("compacted2")
	public void createAjfSolutionCompacted2(
			@Option(name = "named", description = "The name of the new AJF project", required = true) final String name,
			@Option(name = "Directory", required = true) final String projectFolder,
			final PipeOut out) {

		ShellMessages.warn(out, "This is an experimental command...");
		try {
			// String projectFinalName = name + "-" + "ui";
			//
			// File projectDirecory = new File(projectFolder.concat("/").concat(
			// projectFinalName));
			//
			// ShellMessages.info(out,
			// "Creating project in " + projectDirecory.getAbsolutePath());
			// if (!projectDirecory.exists()) {
			// ShellMessages.info(out, "Create project dir :"
			// + projectDirecory.mkdirs());
			// }
			//
			// File pomFile = new File(projectDirecory.getAbsolutePath().concat(
			// "/pom.xml"));
			// ShellMessages.info(out,
			// "Creating pomFile : " + pomFile.createNewFile());
			//
			// ProjectUtils.copyPomFile("initial-pom.xml", pomFile);
			//
			// System.out.println("copy input stream done");
			//
			// // Create project directories
			// // FileUtils.openInputStream(pomFile);
			// FileInputStream fis = new FileInputStream(pomFile);
			// Model pom = new MavenXpp3Reader().read(fis);
			//
			// pom.setArtifactId(projectFinalName);
			// pom.setGroupId(PROJECT_GROUPID_PREFIX);
			// pom.setVersion(START_PROJECT_MILESTONE);
			//
			// /*
			// * Create folders
			// */
			// File srcFolder = new
			// File(projectDirecory.getAbsolutePath().concat(
			// "/src"));
			//
			// System.out.println("** DEBUG create src folder : "
			// + srcFolder.mkdirs());
			//
			// File mainJavaFolder = new
			// File(srcFolder.getAbsolutePath().concat(
			// "/main/java"));
			// System.out.println("** DEBUG create src/main/java : "
			// + mainJavaFolder.mkdirs());
			//
			// File mainResourceFolder = new File(srcFolder.getAbsolutePath()
			// .concat("/main/resources"));
			// System.out.println("** DEBUG create src/main/resource : "
			// + mainResourceFolder.mkdirs());

			ShellMessages.success(out, "Done");

		} catch (Exception e) {
			ShellMessages.error(out, "Error occured : " + e.toString());
		}

	}
}
