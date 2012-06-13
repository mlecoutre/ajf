package am.ajf.forge.plugin;

import static am.ajf.forge.lib.ForgeConstants.PACKAGE_FOR_ENTITY;
import static am.ajf.forge.lib.ForgeConstants.PACKAGE_FOR_MANAGED_BEAN;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_NAME;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_LIB;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_UI;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.WordUtils;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
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

import am.ajf.forge.core.CreateMcr;
import am.ajf.forge.exception.EscapeForgePromptException;
import am.ajf.forge.lib.EntityDTO;
import am.ajf.forge.util.JavaHelper;
import am.ajf.forge.util.ProjectHelper;
import am.ajf.forge.util.ShellHelper;

/**
 * 
 * @author E019851
 * 
 */
@Alias("manage-project")
@Help("Manage an existing ajf project")
public class McrGenerationPlugin implements Plugin {

	@Inject
	private Project uiProject;

	@Inject
	private ProjectFactory projectFactory;

	@Inject
	private ResourceFactory resourceFactory;

	@Inject
	private Shell shell;

	@Inject
	private ShellHelper shellhelper;

	@Inject
	private CreateMcr mcrManagement;

	@Inject
	private ProjectHelper projectHelper;

	@Inject
	private JavaHelper javaUtils;

	@DefaultCommand
	public void show(final PipeOut out) {

		try {
			if (null != uiProject) {

				// Welcome logo
				shell.println();
				shell.println(ShellColor.YELLOW,
						"*****************************************");
				shell.println();
				shell.println(ShellColor.YELLOW,
						"** WELCOME ON AJF2 MCR GENERATION TOOL **");
				shell.println();
				shell.println(ShellColor.YELLOW,
						"*****************************************");
				shell.println();

				// Prompt for which ajf project type
				List<String> choiceList = new ArrayList<String>();
				choiceList.add("Exploded AJF solution");
				choiceList.add("Compacted AJF solution");
				int choice = shell.promptChoice(
						"Which type of ajf project are you working on ?",
						choiceList);

				if (0 == choice)
					shell.execute("manage-project AddFonction");
				else if (1 == choice)
					shell.println("not yet Implemented...");

			} else {
				ShellMessages.error(out,
						"you are not on a correct maven project");
			}
		} catch (Exception e) {

			ShellMessages.error(out, "Error occured : " + e.toString());

		}
	}

	/**
	 * 
	 * @param function
	 * @param entityName
	 * @param out
	 */
	@Command("AddFonction")
	public void createMcrFunction(
			@Option(required = false, name = "function name", shortName = "ft", description = " name of the function p+") String function,
			@Option(required = false, name = "EntityName", shortName = "ent", description = " Name of the entity (i.e : Employee),"
					+ " this must correspond to the java class model") String entityName,
			final PipeOut out) {

		// debug mode
		shell.setVerbose(true);

		try {

			/*
			 * Function Name
			 */
			shell.println();
			promptTitle("Function Generation");
			while (null == function || function.isEmpty()) {
				function = shellhelper
						.promptFacade("Name of the function to generate (i.e: ManagePerson):");
				if (null == function || function.isEmpty()) {
					ShellMessages.warn(out, "This information is required !");
				}
			}

			// get the global project Name (without any suffixe). As we should
			// be on the UI project, we remove the suffixe:
			String ajfSolutionGlobalName = uiProject.getProjectRoot().getName()
					.replace("-".concat(PROJECT_TYPE_UI), "");

			/*
			 * Entity loading
			 */
			shell.println();
			promptTitle("Entity loading");

			// Retrieve the lib component project from the UI (in which we
			// should be)
			Project libProject = projectHelper.locateProjectFromSolution(
					uiProject, projectFactory, resourceFactory,
					PROJECT_TYPE_UI, PROJECT_TYPE_LIB, out);

			// Java facet of the lib project
			JavaSourceFacet libJavaFacet = libProject
					.getFacet(JavaSourceFacet.class);

			// Retrieve the Attributes of the input entity
			EntityDTO entityDto = loadEntity(ajfSolutionGlobalName,
					libJavaFacet, entityName, out);
			entityName = entityDto.getEntityName();

			/*
			 * UNIT TASKS
			 */
			// Prompt user for UT list to generate
			shell.println();
			promptTitle("Unit tasks (UT) List");
			List<String> uts = promptForUTs(entityName, out);

			shell.println();

			/*
			 * PREPARATION FOR GENERATION
			 */

			// Transform function to first letter in upperCase
			function = WordUtils.capitalize(function);

			// Function must not contain the suffix MBean to avoid the double
			// suffix when the managed bean will be generated
			if (function.contains("MBean")) {
				function = shellhelper
						.promptFacade(
								"Function name must not contain the 'MBean' suffix as it will be generated for you when needed. please re-type the function Name:",
								function.replaceAll("MBean", ""));
			}

			// Java facet for ui project
			JavaSourceFacet uiJavaFacet = uiProject
					.getFacet(JavaSourceFacet.class);

			/*
			 * GENERATION
			 */

			// Which package where to create managedBean class
			String managedBeanPackage = shellhelper.promptFacade(
					"Which package of ui project for Managed Bean ?",
					PACKAGE_FOR_MANAGED_BEAN.replace(PROJECT_NAME,
							ajfSolutionGlobalName.toLowerCase()));

			// generate xhtml web page for CRUD
			promptTitle("Xhtml web page generation");
			mcrManagement.generateXhtml(function, entityName, out,
					ajfSolutionGlobalName, entityDto, managedBeanPackage, uts);

			// generate Business Delegate interface for policy
			promptTitle("Business delegate interfaces generation");

			Map<String, String> libPackages = mcrManagement
					.generateBDInterfaceAndDto(function, out,
							ajfSolutionGlobalName, libProject, libJavaFacet,
							entityName, uts);

			// generate policy class
			promptTitle("Policy Class generation");
			mcrManagement.generatePolicy(function, out, uts, libPackages,
					ajfSolutionGlobalName);

			// Generate managed bean for CRUD
			promptTitle("Managed Bean generation");

			mcrManagement.generateManagedBean(function, entityName,
					ajfSolutionGlobalName, entityDto, managedBeanPackage,
					libPackages.get("libBDpackage"),
					libPackages.get("libDTOpackage"), uiJavaFacet, uts, out);

			/*
			 * END
			 */
			ShellMessages.success(out, "Done generated MCR for function:"
					+ function);

		} catch (EscapeForgePromptException esc) {

			shell.println();
			shell.println();
			shell.print(ShellColor.MAGENTA, "***BYE BYE***");
			shell.println();
			shell.println();
			return;

		} catch (Exception e) {

			ShellMessages.error(out,
					"An Exception occured during the AddCrudFonction plugin execution. "
							+ e.toString());
			e.printStackTrace();

		}

	}

	/**
	 * Prompt a beautiful title in the forge prompt
	 * 
	 * @param title
	 */
	private void promptTitle(String title) {

		shell.println();

		shell.println(ShellColor.GREEN, "******************");
		shell.println();
		shell.println(ShellColor.GREEN, "---  " + title + "  ----");
		shell.println();
		shell.println(ShellColor.GREEN, "******************");

	}

	/**
	 * Prompt user and ask for the list of UTs to create. The entity name is
	 * automatically suffixed to the UT name.
	 * 
	 * 
	 * @param entityName
	 * @return
	 * @throws EscapeForgePromptException
	 */
	private List<String> promptForUTs(String entityName, PipeOut out)
			throws EscapeForgePromptException {

		shell.println();
		List<String> uts = new ArrayList<String>();

		// Set to true when we are done with UT
		boolean doneWithUts = false;

		while (!doneWithUts) {
			// Prompt choice
			List<String> choices = new ArrayList<String>();
			choices.add("Add a new Unit Task to my Function.");
			choices.add("Display key unit Tasks for CRUD.");
			choices.add("I'm Done with Unit tasks. Let's continue !");
			int choice = shell.promptChoice("What whould you like to do ?",
					choices);

			// While user ask for adding a new unit tasks
			if (choice == 0) {

				String utvalue = shellhelper
						.promptFacade("UT name (caution: entity Name '"
								+ entityName
								+ "' will be suffixed automatically):");

				shell.println();

				if (utvalue.toLowerCase().contains(entityName.toLowerCase())) {
					// if the ut entered by user already contain entityName
					// (risk of duplicate as entityName will be automatically
					// suffixed)
					ShellMessages
							.warn(out,
									"Please note that the entered entityName will be automatically suffixed to the UT name. Here you're entered UT will be: "
											+ utvalue
											+ WordUtils.capitalize(entityName));

					if (shell.promptBoolean("Is that ok ?", true)) {

						// suffixe entityName to ut
						utvalue = utvalue + WordUtils.capitalize(entityName);
						saveUtToList(entityName, out, uts, utvalue);

					} else {
						ShellMessages.info(out, "The UT has been ignored.");
						shell.println();
					}
				} else if (utvalue.isEmpty()) {
					ShellMessages.error(out, "Empty UT name is not tolerated.");
					shell.println();
				} else {

					// suffixe entityName to ut
					utvalue = utvalue + WordUtils.capitalize(entityName);
					saveUtToList(entityName, out, uts, utvalue);
				}

			} else if (choice == 1) { // Choice to display key UTs
				shell.println();
				shell.println(
						ShellColor.CYAN,
						"Key Unit Tasks are used to generate special functionalities on the web interface, for Creation or Deletion of entities.");
				shell.println();
				shell.print(ShellColor.YELLOW, "'add' or 'create' : ");
				shell.print(
						ShellColor.CYAN,
						" will generate on the web xhtml page the ability to add an entity. In your case, this will generate a creation method such as 'add"
								+ WordUtils.capitalize(entityName)
								+ "' or 'create"
								+ WordUtils.capitalize(entityName) + "'");
				shell.println();
				shell.println();
				shell.print(ShellColor.YELLOW, "'delete' or 'remove' : ");
				shell.print(
						ShellColor.CYAN,
						" will generate on the web xhtml page the ability to Delete an entity. In your case, this will generate a creation method such as 'delete"
								+ WordUtils.capitalize(entityName)
								+ "' or 'remove"
								+ WordUtils.capitalize(entityName) + "'");
				shell.println();
				shell.println();
				shell.println(
						ShellColor.RED,
						"Caution: You'll have either to create your xhtml file for the first time, or if it already exists, to overwrite it, for the modification to take effect.");
				shell.println();
				shell.println();

			} else if (choice == 2) {
				shell.println();
				shell.println("Done.");
				doneWithUts = true;
			}

		}
		return uts;
	}

	/**
	 * Save the entered UT to final UT list. This UT list will be used for
	 * generation
	 * 
	 * @param entityName
	 * @param out
	 * @param uts
	 * @param utvalue
	 */
	private void saveUtToList(String entityName, PipeOut out, List<String> uts,
			String utvalue) {
		// if Ok add ut
		if (!uts.contains(utvalue)) {

			uts.add(utvalue);
			ShellMessages.info(out, "The UT '" + utvalue + "' has been saved.");
			checkIfKeyUt(utvalue, entityName);
		} else {
			ShellMessages.info(out, "The UT " + " was already set.");
		}
		shell.println();
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
	private EntityDTO loadEntity(String ajfSolutionGlobalName,
			JavaSourceFacet libJavaFacet, String entityName, PipeOut out)
			throws Exception {

		// Instanciate the output object
		EntityDTO entityDto = new EntityDTO();

		/*
		 * Locate EntityModel
		 */

		// loop for entity name
		shell.println();
		while (null == entityName || entityName.isEmpty()) {
			entityName = shellhelper
					.promptFacade("Name of Entity, same as model class (i.e: Person):");
			if (null == entityName || entityName.isEmpty()) {
				ShellMessages.warn(out, "This information is required !");
			} else {
				entityName = WordUtils.capitalize(entityName.trim());
			}
		}

		// File libSrcFolder = libJavaFacet.getSourceFolder()
		// .getUnderlyingResourceObject();

		JavaSource modelJavaResource = null;

		// Path of the supposed model (default value)
		String entityPackage = PACKAGE_FOR_ENTITY.replace(PROJECT_NAME,
				ajfSolutionGlobalName.toLowerCase());

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

				shell.println();
				// If model is not found
				ShellMessages.error(out,
						"The Model [" + entityModelPath.replace("/", ".")
								+ "] was not found.");
				shell.println();

				// Ask the user to change the location of the model
				if (shell
						.promptBoolean(
								"Do you want to manually specify the model you want to use [y] ?",
								true)) {

					// Ask for package containing the model in the lib project
					// of ajf solution
					shell.println();
					entityPackage = shellhelper
							.promptFacade(
									"Sub-package of the Project-lib containing the Model class ",
									entityPackage.replace("/", "."));

					// Prompt for model name
					entityName = shellhelper.promptFacade("Model class name",
							WordUtils.capitalize(entityName));

					entityModelPath = entityPackage.concat("/").concat(
							WordUtils.capitalize(entityName) + ".java");

				} else {
					entityModelPath = null;
					// TODO generate a model ?

					shell.println();
					shell.print("Stopping the MCR generation process...");
					throw new EscapeForgePromptException();
				}
			}
		}
		ShellMessages.info(out,
				"Entity ".concat(entityName).concat(" has been loaded."));
		/*
		 * Get Attributes list contained by the entity
		 */
		List<String> entityAttributes = javaUtils
				.retrieveAttributeList(modelJavaResource);

		// Ask user if he wants to keep this attribute in the MCR
		out.println();
		ShellMessages.info(
				out,
				"Entity model ".concat(entityName)
						.concat(" contains " + entityAttributes.size())
						.concat(" attributes :"));

		entityDto.setEntityAttributeList(new ArrayList<String>());
		for (String att : entityAttributes) {
			if (shell.promptBoolean(
					"Use Attribute ".concat(att).concat(" in MCR ? [y]"), true)) {
				// Add attribute to final list if User answer yes

				entityDto.getEntityAttributeList().add(att);
			}

		}
		out.println();

		// entity name has to be set here in case it has been entered several
		// times!
		entityDto.setEntityName(entityName);

		entityAttributes.clear();
		entityAttributes = null;

		return entityDto;

	}

	// /**
	// * Temporary offer a choice to the user. As the Update mode is not yet
	// * implemented, the user is prompted for either exiting the generation
	// * process, either ovewriting the file
	// *
	// * @param out
	// * @throws EscapeForgePromptException
	// */
	// private void temporaryChoiceForUpdate(final PipeOut out)
	// throws EscapeForgePromptException {
	// shell.println();
	// ShellMessages.warn(out,
	// "Update of existing files is not yet supported by AJF forge.");
	// List<String> choices = new ArrayList<String>();
	// choices.add("Exit.");
	// choices.add("Okay, Overwrite the file.");
	// if (shell.promptChoice("What to do ?", choices) == 0) {
	// throw new EscapeForgePromptException();
	// }
	// }

	/**
	 * Key UT values as 'add', 'create', 'delete', or 'remove' are particuliar
	 * values that are checked during the xhtml web file generation. This method
	 * only log an information message to the user if the input UT value he's
	 * entered is one of thos key values
	 * 
	 * @param utvalue
	 * @param entityName
	 */
	private void checkIfKeyUt(String utvalue, String entityName) {

		if (utvalue.startsWith("add" + WordUtils.capitalize(entityName))
				|| utvalue.startsWith("create"
						+ WordUtils.capitalize(entityName))
				|| utvalue.startsWith("remove"
						+ WordUtils.capitalize(entityName))
				|| utvalue.startsWith("delete"
						+ WordUtils.capitalize(entityName))) {

			shell.println(ShellColor.GREEN, "This is a key UT for CRUD");

		}

	}
}
