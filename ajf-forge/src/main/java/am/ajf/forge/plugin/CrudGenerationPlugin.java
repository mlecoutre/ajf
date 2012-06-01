package am.ajf.forge.plugin;

import java.io.File;
import java.io.FileNotFoundException;
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

	@SuppressWarnings("rawtypes")
	@Command("AddCrudFonction")
	public void createCrudFunction(
			@Option(required = true, name = "function name", shortName = "ft", description = " name of the function p+") String function,
			@Option(required = true, name = "EntityName", shortName = "ent", description = " NameOfEntity") String entityName,
			final PipeOut out) {

		try {
			// debug mode
			shell.setVerbose(true);

			if (null == project) {
				throw new Exception(
						"You are not on a correct ajf project instance");
			}
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
			List<String> entityAttributes = retrieveAttributeList(
					ajfSolutionGlobalName, entityName, out);

			JavaSourceFacet javaFacet = project.getFacet(JavaSourceFacet.class);

			// Which package where to create managedBean class
			String managedBeanPackage = shell.prompt(
					"Which package for Managed Bean ?",
					javaFacet.getBasePackage());

			// Way to escape prompt via exit command
			if ("exit".equals(managedBeanPackage.toLowerCase()))
				return;

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
					"Java src folder : " + javaSrcFolder.getAbsolutePath()
							+ "\n");

			// Managed bean class java file
			File managedBeanClassFile = new File(javaSrcFolder
					.getAbsolutePath().concat("/")
					.concat(managedBeanPackage.replace(".", "/")).concat("/")
					.concat(function.concat("MBean") + ".java"));

			// Create directory if needed
			managedBeanClassFile.getParentFile().mkdirs();
			if (!managedBeanClassFile.exists())
				ShellMessages.info(out,
						"Creation of ManagedBean class " + function + " : "
								+ managedBeanClassFile.createNewFile());

			// Build data model for FreeMarker templates

			Map dataModelMap = projectManagement.buildDataModel(
					ajfSolutionGlobalName, function.concat("MBean"),
					entityName, javaUtils.capitalizeDatas(entityAttributes),
					managedBeanPackage);

			// Call the crud managed bean java class generation
			projectManagement.buildCrudManagedBean(managedBeanClassFile,
					dataModelMap);
			dataModelMap = null;
			managedBeanClassFile = null;

			/*
			 * Xhtml File Creation
			 */
			// Find the webApp directory
			WebResourceFacet webFacet = project
					.getFacet(WebResourceFacet.class);
			File webAppDirectory = webFacet.getWebRootDirectory()
					.getUnderlyingResourceObject();

			// xhtml java file
			File myXhtmlFile = new File(webAppDirectory.getAbsolutePath()
					.concat("/").concat(WordUtils.uncapitalize(function))
					.concat("/")
					.concat(WordUtils.uncapitalize(function).concat(".xhtml")));

			ShellMessages.info(out,
					"xhtml file :" + myXhtmlFile.getAbsolutePath());
			webAppDirectory = null;

			// Creation of the xhtml physical file
			if (!myXhtmlFile.exists()) {
				myXhtmlFile.getParentFile().mkdirs();
				myXhtmlFile.createNewFile();
			}

			// Generate the xhtml CRUD file
			dataModelMap = projectManagement.buildDataModel(
					ajfSolutionGlobalName, WordUtils.uncapitalize(function)
							.concat("MBean"), entityName, entityAttributes,
					managedBeanPackage);

			ShellMessages.info(out,
					"Start generating xhtml CRUD file for ".concat(entityName)
							.concat(" : ")
							.concat(myXhtmlFile.getAbsolutePath()));

			// launch the generation
			projectManagement.buildCrudXhtml(myXhtmlFile, dataModelMap);

			dataModelMap = null;
			webAppDirectory = null;

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
	 * Locate in the lib project component, in the package <code>
	 * am.ajf.'projectName'.model</code> the <strong>'entityName'.java</strong>
	 * class corresponding to the input model name.</br> The list of Attributes
	 * contained in this java class is returned (attributes that are linked to
	 * getter or setter in the model)
	 * 
	 * @param ajfSolutionGlobalName
	 * @param entityName
	 * @param out
	 * @return
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("rawtypes")
	protected List<String> retrieveAttributeList(String ajfSolutionGlobalName,
			String entityName, PipeOut out) throws FileNotFoundException {
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
		Project libProject = projectFactory
				.findProject((DirectoryResource) resourceFactory
						.getResourceFrom(libProjectFile));

		uiProjectFile = null;
		libProjectFile = null;
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
		JavaSource modelJavaResource;

		// Path of the supposed model
		String entityModelPath = "am/ajf/"
				.concat(ajfSolutionGlobalName.toLowerCase()).concat("/model")
				.concat("/").concat(WordUtils.capitalize(entityName) + ".java");
		try {

			// This is the entity model as Java source
			modelJavaResource = libJavaFacet.getJavaResource(entityModelPath)
					.getJavaSource();
			entityModelPath = null;
		} catch (FileNotFoundException ex) {
			ShellMessages.error(out,
					"The specified Model Name does not exist ! ["
							+ entityModelPath + "] - " + ex.toString());
			entityModelPath = null;
			// TODO what to do now ? generate a model ?
			throw ex;
		}

		/*
		 * Get Attributes list contained by the entity
		 */
		List<String> entityAttributes = javaUtils
				.retrieveAttributeList(modelJavaResource);
		ShellMessages.info(
				out,
				"Entity model ".concat(entityName)
						.concat(" contains " + entityAttributes.size())
						.concat(" attributes :"));
		for (String retrieveAttributeList : entityAttributes) {
			out.print(ShellColor.YELLOW, retrieveAttributeList);
			out.println();
		}

		return entityAttributes;
	}
}
