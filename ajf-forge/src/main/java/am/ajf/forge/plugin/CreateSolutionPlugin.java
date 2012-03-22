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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.ResourceFacet;
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
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.Files;
import org.jboss.forge.shell.util.ResourceUtil;

import am.ajf.forge.core.CreateProject;
import am.ajf.forge.util.ProjectUtils;

//import org.jboss.forge.shell.plugins.builtin.NewProjectPackagingTypeCompleter;

/**
 * 
 * @author E019851
 * 
 */
@Alias("ajf-solution")
@Topic("Project")
@Help("Create a new AJF solution in selected directory.")
public class CreateSolutionPlugin implements Plugin {

	@Inject
	private Shell shell;

	@Inject
	private ProjectFactory projectFactory;

	@Inject
	private ResourceFactory factory;

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

		boolean isProjectDirFlag = false;
		String AJF_PROJECT_TYPE_SIMPLE = "Simple ajf solution";
		String AJF_PROJECT_TYPE_COMPLEX = "Complex ajf solution";

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
			if (!"exit".equals(projectDirectory)) {
				isProjectDirFlag = true;
				createAjfSolutionCompacted(projectName, projectDirectory, out);
			}

		} else if (AJF_PROJECT_TYPE_COMPLEX.equals(projectType)) {

			String projectName = shell.prompt("Project Name :");
			String projectDirectory = promptProjectDirectory("Project directory (empty is current directory) :");
			if (!"exit".equals(projectDirectory)) {
				isProjectDirFlag = true;
				createAjfSolutionExploded(projectName, projectDirectory, out);
			}

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
	@Command("exploded")
	public void createAjfSolutionExploded(
			@Option(name = "named", description = "The name of the new AJF project", required = true) final String name,
			@Option(name = "Directory", required = true) final String folderName,
			final PipeOut out) {

		// Check project directory
		// if (isProjectDirFlag || checkProjectDirectoryConsistency(folderName))
		// {

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
			ShellMessages.info(out, "AJF solution done.[" + folderName + "]");

		} catch (Exception e) {

			// print on the shell the exception thrown
			ShellMessages.error(out,
					"AJF project generation process has thrown an Exception : "
							+ e.toString());

		}
		// } else {
		//
		// // DO nothing
		//
		// }

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

		// Check project directory
		// if (isProjectDirFlag || checkProjectDirectoryConsistency(folderName))
		// {

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

		// } else {
		// // Do nothing
		// }

	}

	@Command("compacted2")
	public void createAjfSolutionCompacted2(
			@Option(name = "named", description = "The name of the new AJF project", required = true) final String name,
			@Option(name = "Directory", required = true) final String projectFolder,
			final PipeOut out) {

		try {
			String projectFinalName = name + "-" + "ui";
			// DirectoryResource dir =
			// prepareDirectoryResource(projectFinalName,
			// projectFolder, out);

			// got the final directory
			// ShellMessages.info(out,
			// "Creating project in " + dir.getFullyQualifiedName());

			File projectDirecory = new File(projectFolder.concat("/").concat(
					projectFinalName));

			ShellMessages.info(out,
					"Creating project in " + projectDirecory.getAbsolutePath());
			if (!projectDirecory.exists()) {
				ShellMessages.info(out, "Create project dir :"
						+ projectDirecory.mkdirs());
			}

			File pomFile = new File(projectDirecory.getAbsolutePath().concat(
					"/pom.xml"));
			ShellMessages.info(out,
					"Creating pomFile : " + pomFile.createNewFile());

			ProjectUtils.copyPomFile("initial-pom.xml", pomFile);

			System.out.println("copy input stream done");

			// Create project directories
			// FileUtils.openInputStream(pomFile);
			FileInputStream fis = new FileInputStream(pomFile);
			Model pom = new MavenXpp3Reader().read(fis);

			pom.setArtifactId(projectFinalName);
			pom.setGroupId(PROJECT_GROUPID_PREFIX);
			pom.setVersion(START_PROJECT_MILESTONE);

			/*
			 * Create folders
			 */
			File srcFolder = new File(projectDirecory.getAbsolutePath().concat(
					"/src"));

			System.out.println("create src : " + srcFolder.mkdirs());

			File mainFolder = new File(srcFolder.getAbsolutePath().concat(
					"/main/java"));
			System.out.println("create src/main java : " + mainFolder.mkdirs());

			File srcMainResource = new File(projectDirecory.getAbsolutePath()
					.concat("/src"));

		} catch (IOException e1) {
			ShellMessages.error(out, e1.toString());
			e1.printStackTrace();
		} catch (XmlPullParserException e) {
			ShellMessages.error(out, e.toString());
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ShellMessages.success(out, "VOILA");

	}

	/**
	 * Prepare Resource directory for project: this code is mainly duplicated of
	 * jboss forge source code
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
		} else {
			if (myFile.exists()) {
				myFile.delete();
			}
		}

		return isCorrectDirectory;
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

		String projectFinalName = globalProjectName + "-" + projectType;
		// DirectoryResource dir = prepareDirectoryResource(projectFinalName,
		// projectFolder, out);
		// Create the Project directory and resource
		// Generate the Full path of the project
		String projectCompletePath = projectFolder + "/" + projectFinalName;
		File file = new File(projectCompletePath);

		ShellMessages.warn(out, "FACTORY :" + factory.toString());
		Resource<?> projectResource = factory.getResourceFrom(file);
		DirectoryResource dir = prepareDirectoryResource(projectFinalName,
				projectResource, out);

		shell.setCurrentResource(dir);

		// Resource<?> dir1 = factory.getResourceFrom(file);
		// DirectoryResource dir = (DirectoryResource) dir1;
		// Begining of the Name of all the java packages of the project
		String javaPackage = "am." + globalProjectName.replace("-", ".");

		// Call the CreateProject class (out of the Plugin)
		CreateProject createProject = new CreateProject();

		createProject.createAjfProject(globalProjectName, javaPackage, dir,
				projectFactory, projectType, projectFinalName);

		// Log : End of the project creation
		// ShellMessages.success(out, "Project : " + dir.getFullyQualifiedName()
		// + " created.");
		shell.println();
		ShellMessages.success(out, "ok");

	}
}
