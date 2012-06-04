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
import org.jboss.forge.resources.DirectoryResource;
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
import am.ajf.forge.lib.EntityDTO;
import am.ajf.forge.lib.ForgeConstants;
import am.ajf.forge.util.JavaUtils;

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

	CrudGeneration projectManagement = new CrudGeneration();
	JavaUtils javaUtils = new JavaUtils();

	@DefaultCommand
	public void show(final PipeOut out) {

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
			@Option(required = true, name = "EntityName", shortName = "ent", description = " Name of the entity that you want to manage through this function (i.e : Employee),"
					+ " this must correspond to the Model java class") String entityName,
			final PipeOut out) {

		try {
			// debug mode
			shell.setVerbose(true);

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

			// Retrieve the Attributes of the input entity
			EntityDTO entityDto = retrieveAttributeList(ajfSolutionGlobalName,
					entityName, out);

			JavaSourceFacet javaFacet = project.getFacet(JavaSourceFacet.class);

			// Which package where to create managedBean class
			String managedBeanPackage = shell.prompt(
					"Which package for Managed Bean ?",
					javaFacet.getBasePackage());

			// Way to escape prompt via exit command
			if ("exit".equals(managedBeanPackage.toLowerCase()))
				return;

			// Generate managed bean for CRUD
			generateManagedBean(function, entityName, ajfSolutionGlobalName,
					entityDto, managedBeanPackage, javaFacet, out);

			// generate xhtml web page for CRUD
			generateXhtml(function, entityName, out, ajfSolutionGlobalName,
					entityDto, managedBeanPackage);

			// END
			ShellMessages.info(out, "CRUD generated for function:" + function);

		} catch (Exception e) {

			ShellMessages.error(out,
					"An Exception occured during the AddCrudFonction plugin execution. "
							+ e.toString());
			e.printStackTrace();

		}

	}

	/**
	 * Generate XHTML file related to the CRUD associated to inputs
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
	protected void generateXhtml(String function, String entityName,
			final PipeOut out, String ajfSolutionGlobalName,
			EntityDTO entityDto, String managedBeanPackage) throws IOException,
			Exception {
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

		// Creation of the xhtml physical file
		if (!myXhtmlFile.exists()) {
			myXhtmlFile.getParentFile().mkdirs();
			myXhtmlFile.createNewFile();
		}

		// Generate the xhtml CRUD file
		Map dataModelMap = projectManagement.buildDataModel(
				ajfSolutionGlobalName,
				WordUtils.uncapitalize(function).concat("MBean"), entityName,
				entityDto.getEntityAttributeList(), managedBeanPackage,
				entityDto.getEntityLibPackage().replace("/", "."));

		ShellMessages.info(out,
				"Start generating xhtml CRUD file for ".concat(entityName)
						.concat(" : ").concat(myXhtmlFile.getAbsolutePath()));

		// launch the generation
		projectManagement.buildCrudXhtml(myXhtmlFile, dataModelMap);

		dataModelMap = null;
		webAppDirectory = null;
	}

	/**
	 * Generate the managed bean java class related to the input. This is done
	 * using a FreeMarker template
	 * 
	 * @param function
	 * @param entityName
	 * @param ajfSolutionGlobalName
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

		out.print(ShellColor.MAGENTA,
				"Java src folder : " + javaSrcFolder.getAbsolutePath() + "\n");

		// Managed bean class java file
		File managedBeanClassFile = new File(javaSrcFolder.getAbsolutePath()
				.concat("/").concat(managedBeanPackage.replace(".", "/"))
				.concat("/").concat(function.concat("MBean") + ".java"));

		// Create directory if needed
		managedBeanClassFile.getParentFile().mkdirs();
		if (!managedBeanClassFile.exists())
			ShellMessages.info(out, "Creation of ManagedBean class " + function
					+ " : " + managedBeanClassFile.createNewFile());

		// Build data model for FreeMarker templates
		Map dataModelMap = projectManagement.buildDataModel(
				ajfSolutionGlobalName, function.concat("MBean"), entityName,
				javaUtils.capitalizeDatas(entityDto.getEntityAttributeList()),
				managedBeanPackage,
				entityDto.getEntityLibPackage().replace("/", "."));

		// Call the crud managed bean java class generation
		projectManagement.buildCrudManagedBean(managedBeanClassFile,
				dataModelMap);

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
	 * @param entityName
	 * @param out
	 * @return List<String> attributes list corresponding to Entity Model
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	protected EntityDTO retrieveAttributeList(String ajfSolutionGlobalName,
			String entityName, PipeOut out) throws Exception {
		/*
		 * Locate the LIB project from the expoded AJF Solution
		 */
		File uiProjectFile = project.getProjectRoot()
				.getUnderlyingResourceObject();
		File libProjectFile = new File(uiProjectFile
				.getParent()
				.concat("/")
				.concat(uiProjectFile.getName())
				.replace(ForgeConstants.PROJECT_TYPE_UI,
						ForgeConstants.PROJECT_TYPE_LIB));

		// Check if the lib project directory does exist
		if (!libProjectFile.exists()) {
			throw new Exception(
					"The project "
							+ libProjectFile.getName()
							+ " does not exist. Please check that you are in an exploded ajf solution");
		}

		// Load the lib project in the Project forge object
		Project libProject = projectFactory
				.findProject((DirectoryResource) resourceFactory
						.getResourceFrom(libProjectFile));

		uiProjectFile = null;
		libProjectFile = null;
		out.println();
		ShellMessages.info(out,
				"Project lib of your AJF solution has been loaded :"
						+ libProject.getProjectRoot()
								.getUnderlyingResourceObject()
								.getAbsolutePath());
		/*
		 * Locate EntityModel
		 */
		JavaSourceFacet libJavaFacet = libProject
				.getFacet(JavaSourceFacet.class);
		// File libSrcFolder = libJavaFacet.getSourceFolder()
		// .getUnderlyingResourceObject();
		JavaSource modelJavaResource = null;

		// Instanciate the output object
		EntityDTO entityDto = new EntityDTO();

		// Path of the supposed model (default value)
		String entityPackage = "am/ajf/".concat(
				ajfSolutionGlobalName.toLowerCase()).concat("/lib/model");
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
									entityPackage);

					// Prompt for model name
					entityName = shell.prompt("Model class name",
							WordUtils.capitalize(entityName));

					entityModelPath = entityPackage.concat("/").concat(
							WordUtils.capitalize(entityName) + ".java");

				} else {
					entityModelPath = null;
					// TODO what to do now ? generate a model ?
					throw ex;
				}
			}
		}
		/*
		 * Get Attributes list contained by the entity
		 */
		List<String> entityAttributes = javaUtils
				.retrieveAttributeList(modelJavaResource);

		// Ask user if he wants to keep this attribute in the CRUD
		ShellMessages.info(
				out,
				"Entity model ".concat(entityName)
						.concat(" contains " + entityAttributes.size())
						.concat(" attributes :"));

		out.println();

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
