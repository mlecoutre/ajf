package am.ajf.forge.core;

import static am.ajf.forge.lib.ForgeConstants.PACKAGE_FOR_BD_INTERFACES;
import static am.ajf.forge.lib.ForgeConstants.PACKAGE_FOR_DTO;
import static am.ajf.forge.lib.ForgeConstants.PACKAGE_FOR_POLICY;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_NAME;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.WordUtils;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.impl.JavaInterfaceImpl;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.PipeOut;

import am.ajf.forge.core.generators.templates.McrGenerationTemplate;
import am.ajf.forge.exceptions.EscapeForgePromptException;
import am.ajf.forge.helpers.JavaHelper;
import am.ajf.forge.helpers.ShellHelper;
import am.ajf.forge.lib.EntityDTO;
import am.ajf.forge.lib.ForgeConstants;

public class CreateMcr {

	@Inject
	private Project uiProject;

	// @Inject
	// private ProjectFactory projectFactory;
	//
	// @Inject
	// private ResourceFactory resourceFactory;
	//
	// @Inject
	// private ProjectHelper projectHelper;

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
	 * @param entityName
	 * @param ajfSolutionGlobalName
	 * @param entityDto
	 * @param managedBeanPackage
	 * @param libBDpackage
	 * @param libDTOPackage
	 * @param uiJavaFacet
	 * @param uts
	 * @param out
	 * @return boolean
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	public boolean generateManagedBean(String function, String entityName,
			String ajfSolutionGlobalName, EntityDTO entityDto,
			String managedBeanPackage, String libBDpackage,
			String libDTOPackage, JavaSourceFacet uiJavaFacet,
			List<String> uts, final PipeOut out) throws Exception {

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
		File javaSrcFolder = uiJavaFacet.getSourceFolder()
				.getUnderlyingResourceObject();

		// Managed bean class java file
		File managedBeanClassFile = new File(javaSrcFolder.getAbsolutePath()
				.concat("/").concat(managedBeanPackage.replace(".", "/"))
				.concat("/").concat(function.concat("MBean") + ".java"));

		// Create directory if needed
		managedBeanClassFile.getParentFile().mkdirs();
		boolean updateMode = false; // true to update

		if (!managedBeanClassFile.exists()) {

			System.out.println("Physical file creation : "
					+ managedBeanClassFile.createNewFile());

		} else {

			// Ask to update or overwrite
			shell.println("Managed bean java file already exist");
			updateMode = !shell.promptBoolean(
					"Do you whish to overwrite ? [N]", false);
		}

		if (!updateMode) {

			// Call the crud managed bean java class generation
			projectManagement.buildManagedBean(managedBeanClassFile,
					ajfSolutionGlobalName, function, WordUtils
							.capitalize(entityName),
					javaUtils.capitalizeDatas(entityDto
							.getEntityAttributeList()), managedBeanPackage,
					libBDpackage, libDTOPackage, entityDto
							.getEntityLibPackage().replace("/", "."), uts);

			System.out.println("Managed bean generation done.");
			out.println();

			managedBeanClassFile = null;

		} else {

			/*
			 * UPDATE
			 */
			ShellMessages.info(out,
					"Updating ".concat(managedBeanClassFile.getName() + "..."));

			// get JavaSource corresonding to existing java BD class
			JavaSource managedBeanJavaSource = uiJavaFacet.getJavaResource(
					managedBeanPackage.replace(".", "/").concat("/")
							.concat(managedBeanClassFile.getName()))
					.getJavaSource();

			// List of UT that do not already exist in java source, and will
			// be added
			List<String> utToBeAdded = calculateUtListToAdd(out, uts,
					managedBeanJavaSource);

			/*
			 * Update Managed Bean Class with new UTs
			 */
			JavaClass managedBeanJavaclass = (JavaClass) managedBeanJavaSource
					.getOrigin();

			if (utToBeAdded.size() > 0)
				ShellMessages.info(out,
						"Addition of methods for new Unit tasks:");

			// generate 1 empty method for each UT
			javaUtils.updateManagedBean(utToBeAdded, function, entityName,
					libDTOPackage, managedBeanJavaclass, projectManagement);

			// Save updated java class in project
			uiJavaFacet.saveJavaSource(managedBeanJavaclass);
		}

		return true;

	}

	/**
	 * Generate XHTML file for web interface related to the CRUD associated to
	 * inputs. Uses the freemarker template
	 * 
	 * @param function
	 * @param entityName
	 * @param xhtmlType
	 *            must be 'list' or 'create'
	 * @param out
	 *            Pipeout
	 * @param ajfSolutionGlobalName
	 * @param entityAttributes
	 * @param managedBeanPackage
	 * @param uts
	 *            list of UTs entered by user
	 * @throws IOException
	 * @throws Exception
	 */
	public boolean generateXhtml(String function, String entityName,
			String xhtmlType, final PipeOut out, String ajfSolutionGlobalName,
			EntityDTO entityDto, String managedBeanPackage, List<String> uts)
			throws IOException, Exception {

		// In case entityName is not capitalized
		entityName = WordUtils.capitalize(entityName);
		String xhtmlFileName = xhtmlType + entityName + ".xhtml";

		ShellMessages.info(out, "Start generating xhtml file for function : "
				.concat(xhtmlFileName));
		/*
		 * Xhtml File(s) Creation
		 */
		// Find the webApp directory
		WebResourceFacet webFacet = uiProject.getFacet(WebResourceFacet.class);
		File webAppDirectory = webFacet.getWebRootDirectory()
				.getUnderlyingResourceObject();

		// xhtml java file : listEntityName.xhtml
		File xhtmlFile = new File(webAppDirectory.getAbsolutePath().concat("/")
				.concat(WordUtils.uncapitalize(function)).concat("/")
				.concat(xhtmlFileName));

		webAppDirectory = null;

		// System.out.println("File path : " + myXhtmlFile.getAbsolutePath());

		// flag - true to generate xhtml file
		boolean generateFileFlag = true;

		// Creation of the xhtml physical file
		if (!xhtmlFile.exists()) {

			xhtmlFile.getParentFile().mkdirs();
			System.out.println(xhtmlFile.getName()
					+ " file creation : ".concat(String.valueOf(xhtmlFile
							.createNewFile())));

		} else if (xhtmlFile.exists()
				&& !shell.promptBoolean("File " + xhtmlFile.getName()

				+ " already exists. Overwrite ? [N]", false)) {
			// xhtml file is not updated
			ShellMessages.info(out, xhtmlFile.getName()
					+ " file will not be modified");
			generateFileFlag = false;
		}

		if (generateFileFlag) {

			// launch the generation via template
			projectManagement.buildXhtml(xhtmlFile, ajfSolutionGlobalName,
					WordUtils.uncapitalize(function).concat("MBean"),
					entityName, entityDto.getEntityAttributeList(),
					managedBeanPackage, entityDto.getEntityLibPackage()
							.replace("/", "."), uts, "list".equals(xhtmlType));

			// Note : "list".equals(xhtmlType) is set to return true in case of
			// generating the list entity string xhtml and false if not which
			// imply the generation of the create entity xhtml

			System.out.println(xhtmlType + " Xhtml file generation done.");
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
	 * @param project
	 *            where to generate java class
	 * @param javaFacet
	 * @param isExploded
	 *            true if we are in an ajf exploded solution
	 * @return Map which key 'libBDpackage' is linked to the BD interfaces
	 *         package and the 'libDtoPackage' key which is linked to the DTO
	 *         Beans objects package
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, String> generateBDInterfaceAndDto(String function,
			final PipeOut out, String ajfSolutionGlobalName, Project project,
			JavaSourceFacet javaFacet, String entityName, List<String> uts,
			boolean isExploded) throws Exception {

		ShellMessages.info(out, "Start generating business delegate interface");
		shell.println();

		Map<String, String> output = new HashMap<String, String>();

		/*
		 * BD interface generation
		 */
		File libSrcFolder = javaFacet.getSourceFolder()
				.getUnderlyingResourceObject();

		// lib package containing BD interfaces (package is entered with '.'
		// separators that we automatically replace by '/')
		String libBDpackagePath;

		if (isExploded) {
			// If we are in exploded solution, we specify that BD interfaces
			// will be created in lib project
			libBDpackagePath = shellhelper.promptFacade(
					"Which package of " + ForgeConstants.PROJECT_TYPE_LIB
							+ " project for BD interfaces ?",
					PACKAGE_FOR_BD_INTERFACES.replace(PROJECT_NAME,
							ajfSolutionGlobalName.toLowerCase())).replace(".",
					"/");
		} else {
			// If compacted project, no need to specify project type
			libBDpackagePath = shellhelper.promptFacade(
					"Which package for BD interfaces ?",
					PACKAGE_FOR_BD_INTERFACES.replace(PROJECT_NAME,
							ajfSolutionGlobalName.toLowerCase())).replace(".",
					"/");
		}

		shell.println();

		File libBusinessFolder = new File(libSrcFolder.getAbsolutePath()
				.concat("/").concat(libBDpackagePath));
		boolean foundLibBDPackage = libBusinessFolder.exists();

		/*
		 * Loop until found package correct package to create BD
		 */
		while (!foundLibBDPackage) {

			if (!libBusinessFolder.exists()) {
				shell.println("Create package : " + libBusinessFolder.mkdirs());

			} else {
				// Go on
				foundLibBDPackage = true;
			}
		}

		// store lib BD package value in output object
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

		/*
		 * Check function BD existence
		 */
		// Case already exist : update it or overwrite it ?
		if (functionBdFile.exists()
				&& !shell
						.promptBoolean(
								"File ".concat(functionBdFile.getName())
										.concat(" already exists. Do you want to overWrite ('N' to update) ? [N]"),
								false)) {

			// UPDATE the function BD interface with new UT (if there are)
			ShellMessages.info(out,
					"Updating ".concat(functionBdFile.getName() + "..."));

			// get JavaSource corresonding to existing java BD class
			functionBdJavaSource = javaFacet.getJavaResource(
					libBDpackagePath.replace(".", "/") + "/"
							+ functionBdFile.getName()).getJavaSource();

			// Calculate list of UT that DOES NOT already exists
			utToBeAdded = calculateUtListToAdd(out, uts, functionBdJavaSource);

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
		 * Before : Creation of Param beans and Result beans (DTOs)
		 */
		shell.println();
		// Prompt for package where to create parambean and resultbean objects
		String libDtoPackage = shellhelper
				.promptFacade(
						"In which package of Lib project you want to create DTOs (ParamBeans, ResultBeans)",
						PACKAGE_FOR_DTO.replace(PROJECT_NAME,
								ajfSolutionGlobalName.toLowerCase()));
		shell.println();

		// File corresponding to package
		File libDtoPackageFile = new File(javaFacet.getSourceFolder()
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

		// store lib DTO package in output
		output.put("libDTOpackage", libDtoPackage.replace("/", "."));

		/*
		 * Generate DTOs beans result beans and Param beans
		 */
		generateDTOs(out, javaFacet, uts, libDtoPackage, libDtoPackageFile);

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
				ShellMessages.info(out, "Addition Unit tasks in BD interface:");

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
			javaFacet.saveJavaSource(javaclass);

		}

		return output;
	}

	/**
	 * * Generate the policy java classwith input uts
	 * 
	 * @param project
	 *            where to generate policy
	 * @param function
	 * @param out
	 * @param uts
	 * @param libPackages
	 * @param ajfSolutionGlobalName
	 * @throws Exception
	 * @throws EscapeForgePromptException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public void generatePolicy(Project project, String function,
			final PipeOut out, List<String> uts,
			Map<String, String> libPackages, String ajfSolutionGlobalName)
			throws Exception, EscapeForgePromptException, IOException {

		ShellMessages.info(out, "Start generating Policy java class");

		shell.println();

		JavaSourceFacet coreJavaFacet = project.getFacet(JavaSourceFacet.class);

		// package where to store Policy
		String corePolicyPackage = shell.prompt(
				"In Which package do you want to generate Policy class ?",
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

			// Calculate list containing new UT for update
			List<String> newUts = calculateUtListToAdd(out, uts,
					policyFileJavaSource);

			// Update Policy with new UT
			JavaClass javaclass = (JavaClass) policyFileJavaSource.getOrigin();
			shell.println();

			for (String ut : newUts) {
				shell.println("Adding UT:" + ut + "...");

				// Add DTO import
				javaclass.addImport(libPackages.get("libDTOpackage") + "."
						+ WordUtils.capitalize(ut) + "PB");
				javaclass.addImport(libPackages.get("libDTOpackage") + "."
						+ WordUtils.capitalize(ut) + "RB");

				// Temp file to store templated method
				File tempFile = new File(FileUtils.getTempDirectoryPath()
						.concat("/ajf-forge/managedBeanMethodForUT.tmp"));
				try {

					// generate method for managed bean for additional UT
					projectManagement.buildPolicyMethod(tempFile, ut);

					// parse method (javaclass containing one method, which is
					// the method we want)
					JavaClass temp = (JavaClass) JavaParser.parse(tempFile);

					// We use the METHOD 0 of the template class as it contains
					// only one
					Method<JavaClass> myMethod = temp.getMethods().get(0);

					// Create method in managedBean class beeing updated
					Method<JavaClass> myMethod2 = javaclass
							.addMethod(
									"public void " + WordUtils.uncapitalize(ut)
											+ "(" + WordUtils.capitalize(ut)
											+ "PB "
											+ WordUtils.uncapitalize(ut)
											+ "pb){}")
							.setReturnType(WordUtils.capitalize(ut) + "RB")
							.addThrows(Exception.class);

					// Set the body thanks to template
					myMethod2.setBody(myMethod.getBody());

				} catch (Exception e) {
					ShellMessages.error(out,
							"Problem occured while generating additional UT "
									.concat(ut + ":").concat(e.toString()));
					throw e;

				} finally {
					// clean temp file
					if (tempFile.exists()) {
						tempFile.delete();
					}
				}

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
	 * This method retrieve the list of method that exists in the input java
	 * source. It checks if methods related to input UT list already exists in
	 * the java source. If it's not, the UT is added to the output 'utToBeAdded'
	 * list. The UT names contained in the output list can be added to the java
	 * source without risk of duplicate.
	 * 
	 * @param out
	 * @param uts
	 * @param javaSource
	 * @return List<String> utToBeAdded
	 */
	@SuppressWarnings("rawtypes")
	private List<String> calculateUtListToAdd(final PipeOut out,
			List<String> uts, JavaSource javaSource) {
		List<String> utToBeAdded;
		// retrieve method list of this java source
		List<String> methodList = javaUtils.retrieveMethodList(javaSource);

		// A second list has to be created to with new method to add during
		// update avoid concurrentModification Exception
		utToBeAdded = new ArrayList<String>();
		utToBeAdded.addAll(uts);
		for (String ut : uts) {
			if (methodList.contains(ut)) {
				utToBeAdded.remove(ut);
				shell.println();
				ShellMessages.info(out, "Ignoring UT=" + ut
						+ " generation as it already exists.");
			}
		}
		return utToBeAdded;
	}

	/**
	 * Generate the DTO objects (ParamBean and ResultBean) in the input package,
	 * corresponding to the list of UTs.
	 * 
	 * @param out
	 * @param javaFacet
	 *            java facet of the project where to generate DTOs
	 * @param uts
	 *            list of UT to generate
	 * @param dtoPackageName
	 *            name of the java package of DTOs (as String, separated with
	 *            points)
	 * @param dtoDestinationPackageFile
	 *            Java file corresponding to DTO package directory
	 * @throws Exception
	 */
	private void generateDTOs(final PipeOut out, JavaSourceFacet javaFacet,
			List<String> uts, String dtoPackageName,
			File dtoDestinationPackageFile) throws Exception {
		/*
		 * Beans DTO generation
		 */
		for (String myUt : uts) {

			// Param Bean File
			File utParamBeanFile = new File(dtoDestinationPackageFile
					.getAbsolutePath().concat("/").concat(myUt + "PB.java"));

			File utResultBeanFile = new File(dtoDestinationPackageFile
					.getAbsolutePath().concat("/").concat(myUt + "RB.java"));

			// Existence verification for both files
			List<File> files = new ArrayList<File>();
			files.add(utParamBeanFile);
			files.add(utResultBeanFile);
			for (File myFile : files) {
				if (myFile.exists()
						&& !shell
								.promptBoolean(
										myFile.getName()
												.concat(" file already exists. OverWrite [N]?"),
										false)) {
					// do nothing
				} else {

					try {
						// Create empty java class
						JavaClass javaclass = JavaParser
								.create(JavaClass.class)
								.setPackage(dtoPackageName.replace("/", "."))
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
						javaFacet.saveJavaSource(javaclass);

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
