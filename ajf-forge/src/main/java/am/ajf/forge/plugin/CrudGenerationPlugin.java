package am.ajf.forge.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.WordUtils;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.impl.JavaInterfaceImpl;
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
public class CrudGenerationPlugin implements Plugin {

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
	private CrudGeneration projectManagement;

	@Inject
	private ProjectHelper projectHelper;

	private JavaHelper javaUtils = new JavaHelper();

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
					shell.execute("manage-project AddCrudFonction");
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
	@Command("AddCrudFonction")
	public void createCrudFunction(
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
					.replace("-".concat(ForgeConstants.PROJECT_TYPE_UI), "");

			/*
			 * Entity loading
			 */
			shell.println();
			promptTitle("Entity loading");

			// Retrieve the lib component project from the UI (in which we
			// should be)
			Project libProject = projectHelper.locateProjectFromSolution(
					uiProject, projectFactory, resourceFactory,
					ForgeConstants.PROJECT_TYPE_UI,
					ForgeConstants.PROJECT_TYPE_LIB, out);

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

			JavaSourceFacet javaFacet = uiProject
					.getFacet(JavaSourceFacet.class);

			/*
			 * GENERATION
			 */

			promptTitle("Managed Bean generation");

			// Which package where to create managedBean class
			String managedBeanPackage = shellhelper.promptFacade(
					"Which package for Managed Bean ?", javaFacet
							.getBasePackage().concat(".controllers"));

			// Generate managed bean for CRUD
			generateManagedBean(function, entityName, ajfSolutionGlobalName,
					entityDto, managedBeanPackage, javaFacet, out);

			// generate xhtml web page for CRUD
			promptTitle("Xhtml web page generation");
			generateXhtml(function, entityName, out, ajfSolutionGlobalName,
					entityDto, managedBeanPackage);

			// generate Business Delegate interface for policy
			promptTitle("Business delegate interfaces generation");
			Map<String, String> libPackages = generateBDInterfaceAndDto(
					function, out, ajfSolutionGlobalName, libProject,
					libJavaFacet, entityName, uts);

			// generate policy class
			promptTitle("Policy Class generation");
			generatePolicy(function, out, uts, libPackages);

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
	 * Prompt user and ask for the list of UTs he wants to create.
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

		while (shell.promptBoolean("Would you like to add a new Unit task ?",
				true)) {

			String utvalue = shellhelper
					.promptFacade("UT name (caution: entity Name '"
							+ entityName + "' will be suffixed automatically):");
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
					// if Ok add ut
					if (!uts.contains(utvalue)) {
						uts.add(utvalue);
						ShellMessages.info(out, "The UT has been saved.");
					} else {
						ShellMessages.info(out, "The UT " + utvalue
								+ "was already set.");
					}
					shell.println();

				} else {
					ShellMessages.info(out, "The UT has been ignored.");
					shell.println();
				}
			} else if (utvalue.isEmpty()) {
				ShellMessages.error(out, "Empty UT name is not tolerated.");
				shell.println();
			} else {
				// add ut
				if (!uts.contains(utvalue)) {
					uts.add(utvalue);
					ShellMessages.info(out, "The UT has been saved.");
				} else {
					ShellMessages.info(out, "The UT " + utvalue
							+ " was already set.");
				}
				shell.println();

			}
		}
		return uts;
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
		else
			shell.println("Managed bean java file already exist");
		// TODO update mode

		// Build data model for FreeMarker templates
		Map dataModelMap = projectManagement.buildDataModel(
				ajfSolutionGlobalName, function.concat("MBean"),
				WordUtils.capitalize(entityName),
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
		WebResourceFacet webFacet = uiProject.getFacet(WebResourceFacet.class);
		File webAppDirectory = webFacet.getWebRootDirectory()
				.getUnderlyingResourceObject();

		// xhtml java file
		File myXhtmlFile = new File(webAppDirectory.getAbsolutePath()
				.concat("/").concat(WordUtils.uncapitalize(function))
				.concat("/")
				.concat(WordUtils.uncapitalize(function).concat(".xhtml")));

		webAppDirectory = null;

		// System.out.println("File path : " + myXhtmlFile.getAbsolutePath());

		// flag - true to generate xhtml file
		boolean generateFileFlag = true;

		// Creation of the xhtml physical file
		if (!myXhtmlFile.exists()) {

			myXhtmlFile.getParentFile().mkdirs();
			System.out.println(myXhtmlFile.getName()
					+ " file creation : ".concat(String.valueOf(myXhtmlFile
							.createNewFile())));

		} else if (myXhtmlFile.exists()
				&& !shell.promptBoolean("File " + myXhtmlFile.getName()
						+ " already exists. Overwrite ?", false)) {
			// xhtml file is not updated
			ShellMessages.info(out, myXhtmlFile.getName()
					+ " file will not be modified");
			generateFileFlag = false;
		}

		if (generateFileFlag) {

			// Generate the xhtml CRUD file
			Map dataModelMap = projectManagement.buildDataModel(
					ajfSolutionGlobalName, WordUtils.uncapitalize(function)
							.concat("MBean"), entityName, entityDto
							.getEntityAttributeList(), managedBeanPackage,
					entityDto.getEntityLibPackage().replace("/", "."));

			System.out.println("Data model Map generated.");

			// launch the generation
			projectManagement.buildCrudXhtml(myXhtmlFile, dataModelMap);

			System.out.println("Xhtml file generation done.");
			out.println();

			dataModelMap = null;
		}

		return true;
	}

	/**
	 * 
	 * generates the BD interface and then generates the DTO result beans and
	 * param beans object in the correct packages
	 * 
	 * @param function
	 * @param out
	 * @param ajfSolutionGlobalName
	 * @param libProject
	 * @param libJavaFacet
	 * @return Map containing libBDpackage wich contains the BD interfaces and
	 *         the libDtoPackage which contains the DTO Beans objects
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	protected Map<String, String> generateBDInterfaceAndDto(String function,
			final PipeOut out, String ajfSolutionGlobalName,
			Project libProject, JavaSourceFacet libJavaFacet,
			String entityName, List<String> uts) throws Exception {

		ShellMessages.info(out, "Start generating business delegate interface");
		shell.println();

		Map<String, String> output = new HashMap<String, String>();

		/*
		 * BD interface generation
		 */
		File libSrcFolder = libJavaFacet.getSourceFolder()
				.getUnderlyingResourceObject();

		// lib package containing BD interfaces
		String libBDpackagePath = shellhelper
				.promptFacade(
						"In which package of project lib you want to generate the BD interfaces ?",
						ajfSolutionGlobalName.toLowerCase()
								.concat("/lib/business").replace("/", "."))
				.replace(".", "/");
		shell.println();

		File libBusinessFolder = new File(libSrcFolder.getAbsolutePath()
				.concat("/").concat(libBDpackagePath));
		boolean foundLibBDPackage = libBusinessFolder.exists();

		// Loop until found package
		while (!foundLibBDPackage) {

			if (!libBusinessFolder.exists()) {

				// error message
				ShellMessages.warn(
						out,
						"the package ".concat(
								libBDpackagePath.replace("/", ".")).concat(
								" of project lib does not exist !"));

				// Ask user to create this new package
				if (shell.promptBoolean(
						"Do you want to create this new Package for BDs ?",
						true)) {

					System.out.println("Create package : "
							+ libBusinessFolder.mkdirs());

				} else {

					// Prompt user for package containing BD
					libBDpackagePath = shellhelper.promptFacade(
							"Sub package of the "
									+ libProject.getProjectRoot().getName()
									+ " that contains the BD interfaces ?",
							ajfSolutionGlobalName.toLowerCase()
									.concat("/lib/business").replace("/", "."))
							.replace(".", "/");

					// physiscal folder corresponding to input
					libBusinessFolder = new File(libSrcFolder.getAbsolutePath()
							.concat("/").concat(libBDpackagePath));
				}

			} else {
				// Go on
				foundLibBDPackage = true;

			}
		}

		// put lib BD package in output
		output.put("libBDpackage", libBDpackagePath.replace("/", "."));

		// Create function BD interface
		File functionBdFile = new File(libBusinessFolder.getAbsolutePath()
				.concat("/" + WordUtils.capitalize(function) + "BD.java"));

		// IF File already exist prompt for choice: what to do ? overwrite or
		// update?
		boolean creationMode = true;
		boolean updateMode = false;
		List<String> utToBeAdded = null; // second list used in case of update
		JavaSource functionBdJavaSource = null; // java source in update mode

		if (functionBdFile.exists()
				&& !shell
						.promptBoolean(
								"File ".concat(functionBdFile.getName())
										.concat(" already exists. Do you want to overWrite ? "),
								false)) {

			// UPDATE MODE
			// temporaryChoiceForUpdate(out);

			functionBdJavaSource = libJavaFacet.getJavaResource(
					libBDpackagePath.replace(".", "/") + "/"
							+ functionBdFile.getName()).getJavaSource();

			List<String> methodList = javaUtils
					.retrieveMethodList(functionBdJavaSource);

			// A second list has to be created to avoid concurrentModification
			// Exception: utTobeAdded in java class
			utToBeAdded = new ArrayList<String>();
			utToBeAdded.addAll(uts);
			for (String ut : uts) {
				if (methodList.contains(ut)) {
					utToBeAdded.remove(ut);
					ShellMessages.warn(out, "Ignoring UT=" + ut
							+ " generation as it already exists.");
				}
			}
			creationMode = false;
			updateMode = true;

		} else if (!functionBdFile.exists()) {

			// if file does not exist, we create it
			System.out.println("Creation of ".concat(functionBdFile.getName()
					+ " : " + functionBdFile.createNewFile()));
			out.println();

		}

		/*
		 * Creation of Param beans and Result beans
		 */
		// Ask for package
		String libDtoPackage = shellhelper
				.promptFacade(
						"In which package of Lib project you want to create DTOs (ParamBeans, ResultBeans)",
						libBDpackagePath.replace("/", ".").concat(".dto"));
		shell.println();

		File libDtoPackageFile = new File(libJavaFacet.getSourceFolder()
				.getUnderlyingResourceObject().getAbsolutePath()
				.concat("/" + libDtoPackage.replace(".", "/")));

		// Creation of package folder if needed
		if (!libDtoPackageFile.exists()) {
			if (libDtoPackageFile.mkdirs()) {
				System.out.println("package ".concat(libDtoPackage).concat(
						" created."));
				shell.println();
			}
		}

		// put lib DTO package in output
		output.put("libDTOpackage", libDtoPackage.replace("/", "."));

		/*
		 * Generate DTOs beans result beans and Param beans
		 */
		generateDTOs(out, libJavaFacet, uts, libDtoPackage, libDtoPackageFile);

		if (creationMode) {

			// Generate BD interface
			projectManagement.buildBusinessDelegateInterface(functionBdFile,
					libBDpackagePath.replace("/", "."), function, uts,
					libDtoPackage.replace("/", "."));

			out.println();
		} else if (updateMode) {

			/*
			 * Update BD interface with new UT
			 */
			JavaInterfaceImpl javaclass = (JavaInterfaceImpl) functionBdJavaSource
					.getOrigin();

			ShellMessages.info(out, "UT to be added:");

			for (String ut : utToBeAdded) {
				shell.println("Adding UT:" + ut + "...");
				javaclass
						.addMethod(
								"public void " + ut + "("
										+ WordUtils.capitalize(ut) + "PB "
										+ WordUtils.capitalize(ut) + "pb)")
						.setReturnType(WordUtils.capitalize(ut) + "RB")
						.addThrows(Exception.class);

				// Add DTO import
				javaclass.addImport(libDtoPackage + "."
						+ WordUtils.capitalize(ut) + "PB");
				javaclass.addImport(libDtoPackage + "."
						+ WordUtils.capitalize(ut) + "RB");

			}
			libJavaFacet.saveJavaSource(javaclass);

		}

		return output;
	}

	/**
	 * 
	 * @param out
	 * @param libJavaFacet
	 * @param uts
	 * @param libDtoPackage
	 * @param libDtoPackageFile
	 * @throws Exception
	 */
	private void generateDTOs(final PipeOut out, JavaSourceFacet libJavaFacet,
			List<String> uts, String libDtoPackage, File libDtoPackageFile)
			throws Exception {
		/*
		 * Beans DTO generation
		 */
		for (String myUt : uts) {

			// Param Bean File
			File utParamBeanFile = new File(libDtoPackageFile.getAbsolutePath()
					.concat("/").concat(myUt + "PB.java"));

			File utResultBeanFile = new File(libDtoPackageFile
					.getAbsolutePath().concat("/").concat(myUt + "RB.java"));

			// Existence verification for both files
			List<File> files = new ArrayList<File>();
			files.add(utParamBeanFile);
			files.add(utResultBeanFile);
			for (File myFile : files) {
				if (myFile.exists()
						&& !shell.promptBoolean(
								myFile.getName().concat(
										" file already exists. OverWrite ?"),
								false)) {
					// do nothing
				} else {

					try {
						// Create empty java class
						JavaClass javaclass = JavaParser
								.create(JavaClass.class)
								.setPackage(libDtoPackage.replace("/", "."))
								.setName(
										WordUtils.capitalize(myFile.getName())
												.replace(".java", ""))
								.getOrigin();

						// implements Serializable with serialVersuin UID
						javaclass.addField().setPrivate().setFinal(true)
								.setStatic(true).setName("serialVersionUID")
								.setType("long").setLiteralInitializer("1L");
						javaclass.addInterface(Serializable.class);

						System.out.println("Creating " + javaclass.getName()
								+ ".java ...");
						libJavaFacet.saveJavaSource(javaclass);

					} catch (Exception e) {
						ShellMessages.error(
								out,
								"Error occured while generating the java class "
										+ myFile.getName() + " : "
										+ e.toString());
						throw e;
					}
				}
			}
		}
		System.out.println("Done.");
	}

	/**
	 * 
	 * Generate the policy java classwith input uts
	 * 
	 * @param function
	 * @param out
	 * @param uts
	 * @param libPackages
	 * @throws Exception
	 * @throws EscapeForgePromptException
	 * @throws IOException
	 */
	protected void generatePolicy(String function, final PipeOut out,
			List<String> uts, Map<String, String> libPackages)
			throws Exception, EscapeForgePromptException, IOException {

		ShellMessages.info(out, "Start generating Policy java class");

		// loading core component project
		Project coreProject = projectHelper.locateProjectFromSolution(
				uiProject, projectFactory, resourceFactory,
				ForgeConstants.PROJECT_TYPE_UI,
				ForgeConstants.PROJECT_TYPE_CORE, out);

		shell.println();

		JavaSourceFacet coreJavaFacet = coreProject
				.getFacet(JavaSourceFacet.class);

		// package where to store Policy
		String corePolicyPackage = shell.prompt("In Which package of "
				+ ForgeConstants.PROJECT_TYPE_CORE
				+ " do you want to generate Policy class ?", coreJavaFacet
				.getBasePackage().concat(".business"));

		// Java file corresponding to the policy package
		File corePolicyPackageFile = new File(coreJavaFacet.getSourceFolder()
				.getUnderlyingResourceObject().getAbsolutePath().concat("/")
				.concat(corePolicyPackage.replace(".", "/")));

		// Create package (folder) if needed
		if (!corePolicyPackageFile.exists()) {
			shell.println();
			shell.print("create package : " + corePolicyPackageFile.mkdirs());
		}

		/*
		 * Load java file
		 */
		File policyFile = new File(corePolicyPackageFile.getAbsolutePath()
				.concat("/").concat(WordUtils.capitalize(function))
				.concat("Policy.java"));

		if (policyFile.exists()
				&& !shell.promptBoolean("Class " + policyFile.getName()
						+ " already exists. OverWrite ?", false)) {

			/*
			 * Update mode for policy java class
			 */
			JavaSource policyFileJavaSource = coreJavaFacet.getJavaResource(
					corePolicyPackage.replace(".", "/") + "/"
							+ policyFile.getName()).getJavaSource();

			List<String> methodNames = javaUtils
					.retrieveMethodList(policyFileJavaSource);

			List<String> newUts = new ArrayList<String>();
			newUts.addAll(uts);
			for (String ut : uts) {
				if (methodNames.contains(ut)) {
					newUts.remove(ut);
					ShellMessages.info(out, "Ignoring UT:" + ut
							+ " as it already exists.");
				}
			}

			/*
			 * Update Policy with new UT
			 */
			JavaClass javaclass = (JavaClass) policyFileJavaSource.getOrigin();

			for (String ut : newUts) {
				shell.println("Adding UT:" + ut + "...");
				javaclass
						.addMethod(
								"public void " + ut + "("
										+ WordUtils.capitalize(ut) + "PB "
										+ WordUtils.capitalize(ut) + "pb)")
						.setReturnType(WordUtils.capitalize(ut) + "RB")
						.addThrows(Exception.class);

				// TODO add imports
				// Add DTO import
				// javaclass.addImport(libDtoPackage + "."
				// + WordUtils.capitalize(ut) + "PB");
				// javaclass.addImport(libDtoPackage + "."
				// + WordUtils.capitalize(ut) + "RB");

			}
			coreJavaFacet.saveJavaSource(javaclass);

		} else if (!policyFile.exists()) {
			// File creation
			shell.print("Creating " + policyFile.getName() + " ...");
			if (policyFile.createNewFile()) {
				shell.print("Done.");
				shell.println();
			}

		}
		/*
		 * Implements policy via freemarker template
		 */
		projectManagement.buildPolicy(policyFile, function, uts,
				libPackages.get("libBDpackage"),
				libPackages.get("libDTOpackage"), corePolicyPackage);
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
	protected EntityDTO loadEntity(String ajfSolutionGlobalName,
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
					.promptFacade("Name of Entity, same is model class (i.e: Person):");
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

				shell.println();
				// If model is not found
				ShellMessages.error(out,
						"The Model [" + entityModelPath.replace("/", ".")
								+ "] was not found.");
				shell.println();

				// Ask the user to change the location of the model
				if (shell
						.promptBoolean("Do you want to manually specify the model you want to use ?")) {

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

	/**
	 * Temporary offer a choice to the user. As the Update mode is not yet
	 * implemented, the user is prompted for either exiting the generation
	 * process, either ovewriting the file
	 * 
	 * @param out
	 * @throws EscapeForgePromptException
	 */
	private void temporaryChoiceForUpdate(final PipeOut out)
			throws EscapeForgePromptException {
		shell.println();
		ShellMessages.warn(out,
				"Update of existing files is not yet supported by AJF forge.");
		List<String> choices = new ArrayList<String>();
		choices.add("Exit.");
		choices.add("Okay, Overwrite the file.");
		if (shell.promptChoice("What to do ?", choices) == 0) {
			throw new EscapeForgePromptException();
		}
	}
}
