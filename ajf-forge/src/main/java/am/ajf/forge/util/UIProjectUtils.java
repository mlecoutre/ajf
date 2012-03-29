package am.ajf.forge.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.ResourceFacet;

import am.ajf.forge.lib.ForgeConstants;

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
	 * Extract the persistence.xml file into the Resource META-INF folder of the
	 * current project
	 * 
	 * @param project
	 * @throws IOException
	 */
	public static void extractPersistenceXmlFile(Project project)
			throws IOException {

		ResourceFacet resFacet = project.getFacet(ResourceFacet.class);

		File resourceFolder = resFacet.getResourceFolder()
				.getUnderlyingResourceObject();

		File metaInfFolder = new File(resourceFolder.getAbsolutePath().concat(
				"/META-INF"));

		unzipFile(ForgeConstants.PERSISTENCE_XML_ZIP, metaInfFolder);

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
