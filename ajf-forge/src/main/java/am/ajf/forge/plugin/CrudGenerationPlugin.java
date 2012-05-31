package am.ajf.forge.plugin;

import java.io.File;

import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
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
	private Shell shell;

	CrudGeneration projectManagement = new CrudGeneration();

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
			@Option(required = true, name = "EntityName", shortName = "ent", description = " NameOfEntity") String entityName,
			final PipeOut out) {

		try {
			// debug mode
			shell.setVerbose(true);

			if (null == project) {
				throw new Exception(
						"You are not on a correct ajf project instance");
			}

			// Transform function to first letter in upperCase
			function = function.substring(0, 1).toUpperCase()
					.concat(function.substring(1));

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

			// if (managedBeanPackage.contains("."))
			// managedBeanPackage = managedBeanPackage.replace(".", "/");

			// In case the java package starts with '.' or '/' it is removed
			if (managedBeanPackage.substring(0, 1).contains("/")
					|| managedBeanPackage.substring(0, 1).contains("."))
				managedBeanPackage = managedBeanPackage.substring(1);

			// SRC folder of the current project
			File javaSrcFolder = javaFacet.getSourceFolder()
					.getUnderlyingResourceObject();

			out.println();
			out.print(ShellColor.MAGENTA,
					"Java src folder : " + javaSrcFolder.getAbsolutePath());

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

			// Call the crud managed bean java class generation
			projectManagement.buildCrudManagedBean(managedBeanClassFile,
					function.concat("MBean"), entityName, managedBeanPackage);
			managedBeanClassFile = null;

			// Find the webApp directory
			WebResourceFacet webFacet = project
					.getFacet(WebResourceFacet.class);
			File webAppDirectory = webFacet.getWebRootDirectory()
					.getUnderlyingResourceObject();

			File myXhtmlFile = new File(webAppDirectory.getAbsolutePath()
					.concat("/").concat(function).concat(".xhtml"));

			webAppDirectory = null;

			if (!myXhtmlFile.exists())
				myXhtmlFile.createNewFile();

			// Generate the xhtml CRUD file
			ShellMessages.info(out,
					"Start generating xhtml CRUD file for ".concat(entityName)
							.concat(" : ")
							.concat(myXhtmlFile.getAbsolutePath()));
			projectManagement.buildCrudXhtml(myXhtmlFile,
					function.concat("MBean"), entityName);
			webAppDirectory = null;

			ShellMessages.info(out, "CRUD generated for function:" + function);

		} catch (Exception e) {

			ShellMessages.error(out,
					"An Exception occured during the AddCrudFonction plugin execution. "
							+ e.toString());

		}

	}
}
