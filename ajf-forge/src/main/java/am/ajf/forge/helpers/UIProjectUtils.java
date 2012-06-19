package am.ajf.forge.helpers;

import static am.ajf.forge.lib.ForgeConstants.PROJECT_WEB_PATH;

import java.io.File;
import java.io.FileNotFoundException;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;

public class UIProjectUtils {

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

			JavaClass javaclass = createjavaManagedBeanClass(javaPackage,
					"ExempleMBean");

			// Save the java class in the project
			javaSourcefacet.saveJavaSource(javaclass);

			System.out.println("**DEBUG : END Java class MBean generated.");

		} catch (Exception e) {
			String message = "Error when generating example java Managed bean file";
			System.err.println("** ERROR : ".concat(message));
			throw new Exception(message, e);
		}

	}

	/**
	 * Generate an example, and empty managed bean class
	 * 
	 * @param javaPackage
	 * @param className
	 * @return JavaClass
	 */
	public static JavaClass createjavaManagedBeanClass(String javaPackage,
			String className) {

		// Create a java class
		JavaClass javaclass = JavaParser
				.create(JavaClass.class)
				.setPackage(javaPackage + ".web.controllers")
				.setName(className)
				.addMethod(
						"public static void exempleMBeanMethod(String[] args) {}")
				.setBody(
						"System.out.println(\"Hi there! This is an AJF Project UI method ")
				.getOrigin();

		// Add the annotation for JSF2 managed bean
		javaclass.addAnnotation("Named");
		javaclass.addImport("javax.inject.Named");

		return javaclass;
	}

	/**
	 * Create an src/main/webapp/WEB-INF package for the input project
	 * 
	 * 
	 * @param project
	 * @return File corresponding to webApp
	 */
	public static File generateWebAppDirectory(Project project) {

		System.out.println("** START - Generate webapp directory");

		File webAppDir = new File(project.getProjectRoot()
				.getUnderlyingResourceObject().getAbsolutePath()
				.concat(PROJECT_WEB_PATH));

		if (!webAppDir.exists()) {
			webAppDir.mkdirs();
		}

		System.out.println("** END - Generate webapp directory");

		return webAppDir;

	}

}
