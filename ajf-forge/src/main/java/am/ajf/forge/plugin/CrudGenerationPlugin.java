package am.ajf.forge.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.WordUtils;
import org.apache.maven.model.Model;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;

import am.ajf.forge.core.CrudGeneration;
import am.ajf.forge.exception.EscapeForgePromptException;
import am.ajf.forge.lib.EntityDTO;
import am.ajf.forge.lib.ForgeConstants;
import am.ajf.forge.util.JavaUtils;
import am.ajf.forge.util.ProjectHelper;

/**
 * 
 * @author E019851
 * 
 */
@Alias("manage-project")
@Help("Manage an existing ajf project")
public class CrudGenerationPlugin implements Plugin {

	@Inject
	private Project project;

	@Inject
	private ProjectFactory projectFactory;

	@Inject
	private ResourceFactory resourceFactory;

	@Inject
	private Shell shell;

	@Inject
	CrudGeneration projectManagement;

	@Inject
	ProjectHelper projectHelper;

	JavaUtils javaUtils = new JavaUtils();

	@DefaultCommand
	public void show(final PipeOut out) {

		// TODO default command interface
		if (null != project) {
			MavenCoreFacet mavenFacet = project.getFacet(MavenCoreFacet.class);
			Model pom = mavenFacet.getPOM();

			ShellMessages.info(out, "You are working on " + pom.getArtifactId()
					+ "project, on version " + pom.getVersion());

			ShellMessages.info(out,
					"Please use one of the plugin command to use plugin.");
		} else {
			ShellMessages.error(out, "you are not on a correct maven project");
		}
	}

	@Command("AddCrudFonction")
	public void createCrudFunction(
			@Option(required = true, name = "function name", shortName = "ft", description = " name of the function p+") String function,
			@Option(required = true, name = "EntityName", shortName = "ent", description = " Name of the entity (i.e : Employee),"
					+ " this must correspond to the java class model") String entityName,
			final PipeOut out) {

		try {
			// debug mode
			shell.setVerbose(true);

			/*
			 * PREPARATION FOR GENERATION
			 */
			// get the global project Name (without any suffixe)
			String ajfSolutionGlobalName = project.getProjectRoot().getName()
					.replace("-ui", "");

			// Transform function to first letter in upperCase
			function = WordUtils.capitalize(function);

			// Function must not contain the suffix MBean to avoid the double
			// suffix when the managed bean will be generated
			if (function.contains("MBean")) {
				function = shell
						.prompt("Function name must not contain the 'MBean' suffix as it will be generated for you when needed. please re-type the function Name:",
								function.replaceAll("MBean", ""));
			}

			JavaSourceFacet javaFacet = project.getFacet(JavaSourceFacet.class);
			// Which package where to create managedBean class
			String managedBeanPackage = shell.prompt(
					"Which package for Managed Bean ?",
					javaFacet.getBasePackage());

			// Way to escape prompt via exit command
			if ("exit".equals(managedBeanPackage.toLowerCase()))
				return;

			// Retrieve the lib component project from the UI (in which we
			// should be)
			Project libProject = projectHelper.locateProjectFromSolution(
					project, projectFactory, resourceFactory,
					ForgeConstants.PROJECT_TYPE_UI,
					ForgeConstants.PROJECT_TYPE_LIB, out);
			// Java facet of the lib project
			JavaSourceFacet libJavaFacet = libProject
					.getFacet(JavaSourceFacet.class);

			/*
			 * GENERATION
			 */

			// Retrieve the Attributes of the input entity
			EntityDTO entityDto = retrieveAttributeList(ajfSolutionGlobalName,
					libJavaFacet, entityName, out);

			// Generate managed bean for CRUD
			generateManagedBean(function, entityName, ajfSolutionGlobalName,
					entityDto, managedBeanPackage, javaFacet, out);

			// generate xhtml web page for CRUD
			generateXhtml(function, entityName, out, ajfSolutionGlobalName,
					entityDto, managedBeanPackage);

			// generate Business Delegate interface for policy
			generateBDInterface(function, out, ajfSolutionGlobalName,
					libProject, libJavaFacet, entityName);

			/*
			 * END
			 */
			ShellMessages.success(out, "CRUD generated for function:"
					+ function);

		} catch (EscapeForgePromptException esc) {

			shell.print(ShellColor.RED, "***BYE BYE***");
			return;

		} catch (Exception e) {

			ShellMessages.error(out,
					"An Exception occured during the AddCrudFonction plugin execution. "
							+ e.toString());
			e.printStackTrace();

		}

	}

	/**
	 * 
	 * @param function
	 * @param out
	 * @param ajfSolutionGlobalName
	 * @param libProject
	 * @param libJavaFacet
	 * @throws Exception
	 */
	private void generateBDInterface(String function, final PipeOut out,
			String ajfSolutionGlobalName, Project libProject,
			JavaSourceFacet libJavaFacet, String entityName) throws Exception {

		ShellMessages.info(out, "Start generating business delegate interface");

		File libSrcFolder = libJavaFacet.getSourceFolder()
				.getUnderlyingResourceObject();

		// lib package containing BD interfaces
		String libBDpackage = ajfSolutionGlobalName.toLowerCase().concat(
				"/lib/business");
		File libBusinessFolder = new File(libSrcFolder.getAbsolutePath()
				.concat("/").concat(libBDpackage));
		boolean foundLibBDPackage = libBusinessFolder.exists();

		// Loop until found package
		while (!foundLibBDPackage) {

			if (!libBusinessFolder.exists()) {

				// error message
				ShellMessages.error(out,
						"the package ".concat(libBDpackage.replace("/", "."))
								.concat("does not exist !"));

				// Ask user to create this new package
				if (shell.promptBoolean(
						"Do you want to create this new Package for BDs ?",
						false)) {

					System.out.println("Create package : "
							+ libBusinessFolder.mkdirs());

				} else {

					// Prompt user for package containing BD
					libBDpackage = shell.prompt(
							"Sub package of the "
									+ libProject.getProjectRoot().getName()
									+ " that contains the BD interfaces ?",
							ajfSolutionGlobalName.toLowerCase()
									.concat("/lib/business").replace("/", "."))
							.replace(".", "/");

					// physiscal folder corresponding to input
					libBusinessFolder = new File(libSrcFolder.getAbsolutePath()
							.concat("/").concat(libBDpackage));
				}

			} else if ("exit".equals(libBDpackage.toLowerCase())) {
				// Exit prompt loop
				throw new EscapeForgePromptException();

			} else {
				// Go on
				foundLibBDPackage = true;
				System.out.println("Lib business package folder : "
						+ libBusinessFolder.getAbsolutePath());
			}
		}

		// Create function BD interface
		File functionBdFile = new File(libBusinessFolder.getAbsolutePath()
				.concat("/" + WordUtils.capitalize(function) + "BD.java"));

		// IF File already exist and user does not want to overwrite it ->
		// Escape
		if (functionBdFile.exists()
				&& !shell
						.promptBoolean("File "
								.concat(functionBdFile.getName())
								.concat(" already exists. Do you want to overWrite ? "))) {

			ShellMessages.info(out, "Stopping the generation process...");
			throw new EscapeForgePromptException();

		} else if (!functionBdFile.exists()) {

			// if file does not exist, we create it
			System.out.println("Creation of ".concat(functionBdFile.getName()
					+ " : " + functionBdFile.createNewFile()));
		} else {

			// TODO update mode
		}

		// TODO This list will be entered by user (manually for the moment)
		List<String> uts = new ArrayList<String>();
		uts.add("list" + WordUtils.capitalize(entityName));
		uts.add("add" + WordUtils.capitalize(entityName));
		uts.add("update" + WordUtils.capitalize(entityName));
		uts.add("delete" + WordUtils.capitalize(entityName));

		projectManagement.buildBusinessDelegateInterface(functionBdFile,
				libBDpackage.replace("/", "."), function, uts);

		out.println();
	}

	/**
	 * Generate XHTML file for web interface related to the CRUD associated to
	 * inputs. Uses the freemarker template
	 * 
	 * @param function
	 * @param entityName
	 * @param out
	 * @param ajfSolutionGlobalName
	 * @param entityAttributes
	 * @param managedBeanPackage
	 * @throws IOException
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	protected boolean generateXhtml(String function, String entityName,
			final PipeOut out, String ajfSolutionGlobalName,
			EntityDTO entityDto, String managedBeanPackage) throws IOException,
			Exception {

		ShellMessages.info(
				out,
				"Start generating xhtml file for function : ".concat(WordUtils
						.uncapitalize(function) + ".xhtml"));
		/*
		 * Xhtml File Creation
		 */
		// Find the webApp directory
		WebResourceFacet webFacet = project.getFacet(WebResourceFacet.class);
		File webAppDirectory = webFacet.getWebRootDirectory()
				.getUnderlyingResourceObject();

		// xhtml java file
		File myXhtmlFile = new File(webAppDirectory.getAbsolutePath()
				.concat("/").concat(WordUtils.uncapitalize(function))
				.concat("/")
				.concat(WordUtils.uncapitalize(function).concat(".xhtml")));

		webAppDirectory = null;

		System.out.println("File path : " + myXhtmlFile.getAbsolutePath());

		// Creation of the xhtml physical file
		if (!myXhtmlFile.exists()) {
			myXhtmlFile.getParentFile().mkdirs();
			System.out.println("Physical file creation : ".concat(String
					.valueOf(myXhtmlFile.createNewFile())));
		}

		// Generate the xhtml CRUD file
		Map dataModelMap = projectManagement.buildDataModel(
				ajfSolutionGlobalName,
				WordUtils.uncapitalize(function).concat("MBean"), entityName,
				entityDto.getEntityAttributeList(), managedBeanPackage,
				entityDto.getEntityLibPackage().replace("/", "."));

		System.out.println("Data model Map generated.");

		// launch the generation
		projectManagement.buildCrudXhtml(myXhtmlFile, dataModelMap);

		System.out.println("Managed bean generation done.");
		out.println();

		dataModelMap = null;
		webAppDirectory = null;

		return true;
	}

	/**
	 * Generate the managed bean java class related to the input. This is done
	 * using a FreeMarker template
	 * 
	 * @param function
	 *            for crud
	 * @param entityName
	 *            that is managed in Crud
	 * @param ajfSolutionGlobalName
	 *            name of ajf solution
	 * @param entityAttributes
	 * @param managedBeanPackage
	 * @param javaFacet
	 * @param out
	 * @return boolean
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	protected boolean generateManagedBean(String function, String entityName,
			String ajfSolutionGlobalName, EntityDTO entityDto,
			String managedBeanPackage, JavaSourceFacet javaFacet,
			final PipeOut out) throws Exception {

		ShellMessages.info(out, "Start generating ManagedBean class "
				+ function + ".java");
		/*
		 * Managed Bean creation
		 */
		// In case the java package starts with '.' or '/' it is removed
		if (managedBeanPackage.substring(0, 1).contains("/")
				|| managedBeanPackage.substring(0, 1).contains("."))
			managedBeanPackage = managedBeanPackage.substring(1);

		// SRC folder of the current project
		File javaSrcFolder = javaFacet.getSourceFolder()
				.getUnderlyingResourceObject();

		// Managed bean class java file
		File managedBeanClassFile = new File(javaSrcFolder.getAbsolutePath()
				.concat("/").concat(managedBeanPackage.replace(".", "/"))
				.concat("/").concat(function.concat("MBean") + ".java"));

		// Create directory if needed
		managedBeanClassFile.getParentFile().mkdirs();
		if (!managedBeanClassFile.exists())
			System.out.println("Physical file creation : "
					+ managedBeanClassFile.createNewFile());

		// Build data model for FreeMarker templates
		Map dataModelMap = projectManagement.buildDataModel(
				ajfSolutionGlobalName, function.concat("MBean"), entityName,
				javaUtils.capitalizeDatas(entityDto.getEntityAttributeList()),
				managedBeanPackage,
				entityDto.getEntityLibPackage().replace("/", "."));
		System.out.println("Data model Map generated.");

		// Call the crud managed bean java class generation
		projectManagement.buildCrudManagedBean(managedBeanClassFile,
				dataModelMap);

		System.out.println("Managed bean generation done.");
		out.println();

		dataModelMap = null;
		managedBeanClassFile = null;

		return true;

	}

	/**
	 * Locate in the lib project component, in the package <code>
	 * am.ajf.'projectName'.model</code> the <strong>'entityName'.java</strong>
	 * class corresponding to the input model name.</br> The list of Attributes
	 * contained in this java class is returned (attributes that are linked to
	 * getter or setter in the model)
	 * 
	 * @param ajfSolutionGlobalName
	 * @param libJavaFacet
	 *            JavaSourceFacet corresponding to the lib component project of
	 *            the global exploded ajf solution lib component project of the
	 *            same ajf exploded solution
	 * @param entityName
	 * @param out
	 * @return List<String> attributes list corresponding to Entity Model
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	protected EntityDTO retrieveAttributeList(String ajfSolutionGlobalName,
			JavaSourceFacet libJavaFacet, String entityName, PipeOut out)
			throws Exception {

		/*
		 * Locate EntityModel
		 */

		// File libSrcFolder = libJavaFacet.getSourceFolder()
		// .getUnderlyingResourceObject();

		JavaSource modelJavaResource = null;

		// Instanciate the output object
		EntityDTO entityDto = new EntityDTO();

		// Path of the supposed model (default value)
		String entityPackage = ajfSolutionGlobalName.toLowerCase().concat(
				"/lib/model");
		String entityModelPath = entityPackage.concat("/").concat(
				WordUtils.capitalize(entityName) + ".java");

		// loop for finding entity model
		boolean loopForEntity = true;
		while (loopForEntity) {

			try {

				// This is the entity model as Java source
				modelJavaResource = libJavaFacet.getJavaResource(
						entityModelPath).getJavaSource();

				loopForEntity = false;
				entityDto.setEntityLibPackage(entityPackage);

				entityModelPath = null;
				entityPackage = null;

			} catch (FileNotFoundException ex) {

				out.println();
				// If model is not found
				ShellMessages.error(out, "The Model [" + entityModelPath
						+ "] was not found.");

				// Ask the user to change the location of the model
				if (shell
						.promptBoolean("Do you want to manually specify the model you want to use ?")) {

					// Ask for package containing the model in the lib project
					// of ajf solution
					entityPackage = shell
							.prompt("Sub-package of the Project-lib containing the Model class ",
									entityPackage.replace("/", "."));

					// Prompt for model name
					entityName = shell.prompt("Model class name",
							WordUtils.capitalize(entityName));

					entityModelPath = entityPackage.concat("/").concat(
							WordUtils.capitalize(entityName) + ".java");

				} else {
					entityModelPath = null;
					// TODO what to do now ? generate a model ?

					shell.println();
					shell.print("Stopping the CRUD generation process...");
					throw new EscapeForgePromptException();
				}
			}
		}
		/*
		 * Get Attributes list contained by the entity
		 */
		List<String> entityAttributes = javaUtils
				.retrieveAttributeList(modelJavaResource);

		// Ask user if he wants to keep this attribute in the CRUD
		out.println();
		ShellMessages.info(
				out,
				"Entity model ".concat(entityName)
						.concat(" contains " + entityAttributes.size())
						.concat(" attributes :"));

		entityDto.setEntityAttributeList(new ArrayList<String>());
		for (String att : entityAttributes) {
			if (shell
					.promptBoolean(
							"Use Attribute ".concat(att).concat(
									" in CRUD ? [empty=y]"), true)) {
				// Add attribute to final list if User answer yes

				entityDto.getEntityAttributeList().add(att);
			}

		}
		out.println();

		entityAttributes.clear();
		entityAttributes = null;

		return entityDto;

	}
}
