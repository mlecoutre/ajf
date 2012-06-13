package am.ajf.forge.core;

import static am.ajf.forge.lib.ForgeConstants.PACKAGE_FOR_BD_INTERFACES;
import static am.ajf.forge.lib.ForgeConstants.PACKAGE_FOR_DTO;
import static am.ajf.forge.lib.ForgeConstants.PACKAGE_FOR_POLICY;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_NAME;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CORE;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_UI;

import java.io.File;
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
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.PipeOut;

import am.ajf.forge.core.generators.templates.McrGenerationTemplate;
import am.ajf.forge.exception.EscapeForgePromptException;
import am.ajf.forge.lib.EntityDTO;
import am.ajf.forge.util.JavaHelper;
import am.ajf.forge.util.ProjectHelper;
import am.ajf.forge.util.ShellHelper;

public class CreateMcr {

	@Inject
	private Project uiProject;

	@Inject
	private ProjectFactory projectFactory;

	@Inject
	private ResourceFactory resourceFactory;

	@Inject
	private ProjectHelper projectHelper;

	@Inject
	private Shell shell;

	@Inject
	private ShellHelper shellhelper;

	@Inject
	private McrGenerationTemplate projectManagement;

	@Inject
	private JavaHelper javaUtils;

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
	public boolean generateManagedBean(String function, String entityName,
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
		if (!managedBeanClassFile.exists()) {
			System.out.println("Physical file creation : "
					+ managedBeanClassFile.createNewFile());

			// Build data model for FreeMarker templates
			Map dataModelMap = projectManagement.buildDataModel(
					ajfSolutionGlobalName, function.concat("MBean"), WordUtils
							.capitalize(entityName),
					javaUtils.capitalizeDatas(entityDto
							.getEntityAttributeList()), managedBeanPackage,
					entityDto.getEntityLibPackage().replace("/", "."));

			System.out.println("Data model Map generated.");

			// Call the crud managed bean java class generation
			projectManagement.buildManagedBean(managedBeanClassFile,
					dataModelMap);

			System.out.println("Managed bean generation done.");
			out.println();

			dataModelMap = null;
			managedBeanClassFile = null;
		} else {
			shell.println("Managed bean java file already exist");
			// TODO update mode

		}

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
	 * @param uts
	 *            list of UTs entered by user
	 * @throws IOException
	 * @throws Exception
	 */
	public boolean generateXhtml(String function, String entityName,
			final PipeOut out, String ajfSolutionGlobalName,
			EntityDTO entityDto, String managedBeanPackage, List<String> uts)
			throws IOException, Exception {

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

			// launch the generation via template
			projectManagement.buildXhtml(myXhtmlFile, ajfSolutionGlobalName,
					WordUtils.uncapitalize(function).concat("MBean"),
					entityName, entityDto.getEntityAttributeList(),
					managedBeanPackage, entityDto.getEntityLibPackage()
							.replace("/", "."), uts);

			System.out.println("Xhtml file generation done.");
			out.println();

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
	public Map<String, String> generateBDInterfaceAndDto(String function,
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

		// lib package containing BD interfaces (package is entered with '.'
		// separators that we automatically replace by '/')
		String libBDpackagePath = shellhelper
				.promptFacade(
						"In which package of project lib you want to generate the BD interfaces ?",
						PACKAGE_FOR_BD_INTERFACES.replace(PROJECT_NAME,
								ajfSolutionGlobalName.toLowerCase())).replace(
						".", "/");

		shell.println();

		// TODO BACKUP TO REMOVE LATER
		// ajfSolutionGlobalName.toLowerCase()
		// .concat("/lib/business").replace("/", "."))
		// .replace(".", "/")

		File libBusinessFolder = new File(libSrcFolder.getAbsolutePath()
				.concat("/").concat(libBDpackagePath));
		boolean foundLibBDPackage = libBusinessFolder.exists();

		// Loop until found package
		while (!foundLibBDPackage) {

			if (!libBusinessFolder.exists()) {

				// // error message
				// ShellMessages.info(
				// out,
				// "the package ".concat(
				// libBDpackagePath.replace("/", ".")).concat(
				// " of project lib does not exist !"));
				//
				// // Ask user to create this new package
				// if (shell.promptBoolean(
				// "Do you want to create this new Package for BDs [y]?",
				// true)) {

				shell.println("Create package : " + libBusinessFolder.mkdirs());

				// } else {
				//
				// // lib package containing BD interfaces (package is entered
				// // with '.'separators that we automatically replace by '/')
				// libBDpackagePath = shellhelper
				// .promptFacade(
				// "In which package of project lib you want to generate the BD interfaces ?",
				// PACKAGE_FOR_BD_INTERFACES.replace(
				// PROJECT_NAME,
				// ajfSolutionGlobalName.toLowerCase()))
				// .replace(".", "/");
				//
				// // physiscal folder corresponding to input
				// libBusinessFolder = new File(libSrcFolder.getAbsolutePath()
				// .concat("/").concat(libBDpackagePath));
				// }

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
										.concat(" already exists. Do you want to overWrite ? [N]"),
								false)) {

			// UPDATE MODE
			ShellMessages.info(out,
					"Updating ".concat(functionBdFile.getName() + "..."));

			// get JavaSource corresonding to existing java BD class
			functionBdJavaSource = libJavaFacet.getJavaResource(
					libBDpackagePath.replace(".", "/") + "/"
							+ functionBdFile.getName()).getJavaSource();

			// retrieve method list of this java source
			List<String> methodList = javaUtils
					.retrieveMethodList(functionBdJavaSource);

			// A second list has to be created to with new method to add during
			// update avoid concurrentModification Exception
			utToBeAdded = new ArrayList<String>();
			utToBeAdded.addAll(uts);
			for (String ut : uts) {
				if (methodList.contains(ut)) {
					utToBeAdded.remove(ut);
					ShellMessages.warn(out, "Ignoring UT=" + ut
							+ " generation as it already exists.");
				}
			}
			// flags
			creationMode = false;
			updateMode = true;

		} else if (!functionBdFile.exists()) {

			// if file does not exist, we create it
			shell.println("Creation of ".concat(functionBdFile.getName()
					+ " : " + functionBdFile.createNewFile()));
			shell.println();

		}

		/*
		 * Creation of Param beans and Result beans
		 */
		// Prompt for package where to create parambean and resultbean objects
		String libDtoPackage = shellhelper
				.promptFacade(
						"In which package of Lib project you want to create DTOs (ParamBeans, ResultBeans)",
						PACKAGE_FOR_DTO.replace(PROJECT_NAME,
								ajfSolutionGlobalName.toLowerCase()));
		shell.println();

		// File corresponding to package
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

			if (utToBeAdded.size() > 0)
				ShellMessages.info(out, "Addition of DTOs for new Unit tasks:");

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
	@SuppressWarnings("rawtypes")
	public void generatePolicy(String function, final PipeOut out,
			List<String> uts, Map<String, String> libPackages,
			String ajfSolutionGlobalName) throws Exception,
			EscapeForgePromptException, IOException {

		ShellMessages.info(out, "Start generating Policy java class");

		// loading core component project
		Project coreProject = projectHelper.locateProjectFromSolution(
				uiProject, projectFactory, resourceFactory, PROJECT_TYPE_UI,
				PROJECT_TYPE_CORE, out);

		shell.println();

		JavaSourceFacet coreJavaFacet = coreProject
				.getFacet(JavaSourceFacet.class);

		// package where to store Policy
		String corePolicyPackage = shell.prompt(
				"In Which package of " + PROJECT_TYPE_CORE
						+ " do you want to generate Policy class ?",
				PACKAGE_FOR_POLICY.replace(PROJECT_NAME,
						ajfSolutionGlobalName.toLowerCase()));

		// Java file corresponding to the policy package
		File corePolicyPackageFile = new File(coreJavaFacet.getSourceFolder()
				.getUnderlyingResourceObject().getAbsolutePath().concat("/")
				.concat(corePolicyPackage.replace(".", "/")));

		// Create package (folder) if needed
		if (!corePolicyPackageFile.exists()) {
			shell.println();
			shell.println("create package : " + corePolicyPackageFile.mkdirs());
		}

		/*
		 * Load java file
		 */
		File policyFile = new File(corePolicyPackageFile.getAbsolutePath()
				.concat("/").concat(WordUtils.capitalize(function))
				.concat("Policy.java"));

		if (policyFile.exists()
				&& !shell.promptBoolean("Class " + policyFile.getName()
						+ " already exists. OverWrite ? [N]", false)) {
			/*
			 * Update mode for policy java class
			 */
			shell.println("Updating " + policyFile.getName() + " ...");
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
					shell.println();
					ShellMessages.info(out, "Ignoring UT:" + ut
							+ " as it already exists.");
				}
			}

			// Update Policy with new UT
			JavaClass javaclass = (JavaClass) policyFileJavaSource.getOrigin();
			shell.println();

			for (String ut : newUts) {
				shell.println("Adding UT:" + ut + "...");
				javaclass
						.addMethod(
								"public void " + ut + "("
										+ WordUtils.capitalize(ut) + "PB "
										+ WordUtils.capitalize(ut) + "pb)")
						.setReturnType(WordUtils.capitalize(ut) + "RB")
						.addThrows(Exception.class);

				// Add DTO import
				javaclass.addImport(libPackages.get("libDTOpackage") + "."
						+ WordUtils.capitalize(ut) + "PB");
				javaclass.addImport(libPackages.get("libDTOpackage") + "."
						+ WordUtils.capitalize(ut) + "RB");

			}
			coreJavaFacet.saveJavaSource(javaclass);
			shell.println();

		} else if (!policyFile.exists()) {
			// File creation
			shell.println("Creating " + policyFile.getName() + " ...");
			if (policyFile.createNewFile()) {
				shell.println("Done.");
				shell.println();
			}
			/*
			 * Implements policy via freemarker template
			 */
			projectManagement.buildPolicy(policyFile, function, uts,
					libPackages.get("libBDpackage"),
					libPackages.get("libDTOpackage"), corePolicyPackage);
		} else {
			/*
			 * Implements policy via freemarker template
			 */
			shell.println("Overwriting " + policyFile.getName() + " ...");
			projectManagement.buildPolicy(policyFile, function, uts,
					libPackages.get("libBDpackage"),
					libPackages.get("libDTOpackage"), corePolicyPackage);
		}

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

}
