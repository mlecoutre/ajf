package am.ajf.forge.util;

import static am.ajf.forge.lib.ForgeConstants.AJF_CORE;
import static am.ajf.forge.lib.ForgeConstants.AJF_INJECTION;
import static am.ajf.forge.lib.ForgeConstants.AJF_PERSISTENCE;
import static am.ajf.forge.lib.ForgeConstants.AJF_TESTING;
import static am.ajf.forge.lib.ForgeConstants.AJF_WEB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;

public class UIProjectUtils {

	/**
	 * Unzip zip file corresponding to input resourceZipFileName to the
	 * destination folder set as input parameter
	 * 
	 * @param resourceZipFileName
	 *            : Path to the zip resource to be unzipped
	 * @param destinationFolder
	 *            : where to unzip
	 * @throws IOException
	 */
	public static boolean unzipFile(String resourceZipFileName,
			File destinationFolder) throws IOException {

		InputStream resourceZipFileStream = UIProjectUtils.class
				.getClassLoader().getResourceAsStream(resourceZipFileName);

		byte[] buf = new byte[1024];

		ZipEntry zipentry;
		ZipInputStream zipInputStream = new ZipInputStream(
				resourceZipFileStream);

		zipentry = zipInputStream.getNextEntry();

		while (zipentry != null) {
			// for each entry to be extracted
			String entryName = zipentry.getName();
			// System.out.println("entryname " + entryName);
			int n;
			FileOutputStream fileoutputstream;
			File newFile = new File(entryName);
			String directory = newFile.getParent();

			if (directory == null) {
				if (newFile.isDirectory())
					break;
			}

			File myFile = new File(destinationFolder + "/" + entryName);

			if (!myFile.isDirectory() && entryName.contains(".")) {

				if (!myFile.exists()) {
					myFile.getParentFile().mkdirs();
					myFile.createNewFile();
				}
				fileoutputstream = new FileOutputStream(myFile);

				while ((n = zipInputStream.read(buf, 0, 1024)) > -1)
					fileoutputstream.write(buf, 0, n);

				fileoutputstream.close();
				fileoutputstream = null;
			}
			zipInputStream.closeEntry();
			zipentry = zipInputStream.getNextEntry();
			myFile = null;
			newFile = null;

		}// while

		zipInputStream.close();

		zipInputStream = null;
		zipentry = null;

		return true;

	}

	/**
	 * Uses model resources files and sets the dependencies, properties,
	 * profiles and the whole build sections to the generated project's pom.xml
	 * file. AJF dependencies are taken from the ajfDependencies model xml file.
	 * All the other pom settings (dependencies, profiles, build...) are taken
	 * from the ui pom model file.
	 * 
	 * @param project
	 * @param uiModelPomFile
	 *            example ui pom.xml file, containing all maven settings such as
	 *            external dependencies, profiles, build, plugins... (without
	 *            AJF)
	 * @param ajfDependenciesFiles
	 *            sample pom.xml file containint all possible AJF dependencies
	 * @throws Exception
	 */
	public static void setUIPomFromFile(Project project, String uiModelPomFile,
			String ajfDependenciesFiles, boolean isCompacted) throws Exception {

		// Model Pom file corresponding to UI POM (without AJF deps)
		Model uiModelPom = ProjectUtils.getPomFromFile(uiModelPomFile);
		// Model Pom file containing all the ajf dependencies
		Model ajfDepsPom = ProjectUtils.getPomFromFile(ajfDependenciesFiles);

		// Get the current generated project's pom file
		MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
		Model generatedPom = mavenCoreFacet.getPOM();

		// Set Properties only in compacted mode (when not compacted, the
		// properties are set in parent project)
		if (isCompacted)
			generatedPom.setProperties(ajfDepsPom.getProperties());

		// Set Ajf Dependencies for web Project, using model file containing ajf
		// deps
		List<String> ajfDepsWeb = new ArrayList<String>();
		ajfDepsWeb.add(AJF_WEB);
		ajfDepsWeb.add(AJF_CORE);
		ajfDepsWeb.add(AJF_INJECTION);
		ajfDepsWeb.add(AJF_PERSISTENCE);
		ajfDepsWeb.add(AJF_TESTING);
		ProjectUtils.addAjfDependenciesToPom(ajfDepsWeb, ajfDependenciesFiles,
				generatedPom);

		// Set dependencies (other than AJF, specific for web project)
		for (Dependency dep : uiModelPom.getDependencies()) {
			generatedPom.getDependencies().add(dep);
		}

		// set Profiles for web project
		generatedPom.setProfiles(uiModelPom.getProfiles());

		// set build Plugins
		// generatedPom.getBuild().setPlugins(resourcePom.getBuild().getPlugins());
		generatedPom.setBuild(uiModelPom.getBuild());

		mavenCoreFacet.setPOM(generatedPom);
	}

	/**
	 * Generate a managed bean class to the current project. It uses as package
	 * name the javaPackage input params, suffixed of web.controllers :
	 * 
	 * 'javaPackage'.web.controllers.
	 * 
	 * @param javaPackage
	 * @param project
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	public static void generateManagedBeanClass(String javaPackage,
			Project project) throws Exception {

		try {
			System.out.println("** DEBUG : START generating java class for UI");

			JavaSourceFacet javaSourcefacet = project
					.getFacet(JavaSourceFacet.class);

			// Create a java class
			JavaClass javaclass = JavaParser
					.create(JavaClass.class)
					.setPackage(javaPackage + ".web.controllers")
					.setName("ExempleMBean")
					.addMethod(
							"public static void exempleMBeanMethod(String[] args) {}")
					.setBody(
							"System.out.println(\"Hi there! This is an AJF Project UI method ")
					.getOrigin();

			// Add the annotation for JSF2 managed bean
			javaclass.addAnnotation("Named");

			javaclass.addImport("javax.inject.Named");

			// Save the java class in the project
			javaSourcefacet.saveJavaSource(javaclass);

			System.out.println("**DEBUG : END Java class MBean generated.");

		} catch (Exception e) {
			String message = "Error when generating example java Managed bean file";
			System.err.println("** ERROR : ".concat(message));
			throw new Exception(message, e);
		}

	}

}
