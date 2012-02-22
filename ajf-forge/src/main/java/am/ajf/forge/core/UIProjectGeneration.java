package am.ajf.forge.core;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.io.FileUtils;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;

import am.ajf.forge.util.ProjectUtils;

public class UIProjectGeneration {

	/**
	 * Generate AJF UI Project
	 * 
	 * @param globalProjectName
	 * @param projectFinalName
	 * @param javaPackage
	 * @param projectFactory
	 * @param projectType
	 * @param dir
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Project generateProjectUI(String globalProjectName,
			String projectFinalName, String javaPackage,
			ProjectFactory projectFactory, String projectType,
			DirectoryResource dir) {

		// Create Project
		Project project;
		project = projectFactory
				.createProject(dir, DependencyFacet.class, MetadataFacet.class,
						JavaSourceFacet.class, ResourceFacet.class);

		// Set project meta data in pom
		ProjectUtils.setBasicProjectData(globalProjectName, projectFinalName,
				project);

		PackagingFacet packaging = project.getFacet(PackagingFacet.class);
		packaging.setPackagingType(PackagingType.WAR);

		// Set name of the project
		packaging.setFinalName(projectFinalName);

		// Set the pom parent
		ProjectUtils.setPomParent(globalProjectName, project);

		/*
		 * Set internal dependencies linked to other project of the AJF-solution
		 */
		ProjectUtils.addInternalDependency(globalProjectName, project,
				ProjectUtils.PROJECT_TYPE_CONFIG);
		ProjectUtils.addInternalDependency(globalProjectName, project,
				ProjectUtils.PROJECT_TYPE_CORE);

		/*
		 * Create a java class with a method
		 */
		generateManagedBeanClass(javaPackage, project);

		try {
			/*
			 * WEB part
			 */
			// // Create webapp/Webinf directories
			// String projectDirectory = project.getProjectRoot()
			// .getFullyQualifiedName();
			// File webAppDir = new File(projectDirectory + "/main/webapp");
			//
			// if (!webAppDir.exists())
			// webAppDir.mkdirs();
			//
			// // Web-in template
			// File mytemplateDir = new File(webAppDir.getAbsolutePath().concat(
			// "/WEB-INF/templates"));
			//
			// if (!mytemplateDir.exists())
			// mytemplateDir.mkdirs();
			//
			// // SimpleLayout - Copy simpleLayoutFile to destination project
			// File mySimpleLayoutFile = new
			// File(CreateProject.class.getResource(
			// "SimpleLayout.xhtml").toURI());
			// FileUtils.copyFileToDirectory(mySimpleLayoutFile, mytemplateDir);

			/*
			 * JSF Dependency
			 */
			System.out.println("Adding JSF dependencies...");
			ProjectUtils.addDependency(project, "javax.faces", "jsf-api",
					"2.0.3");
			ProjectUtils.addDependency(project, "javax.faces", "jsf-impl",
					"2.0.3");
			System.out.println("JSF dependencies added.");

		} catch (Exception e) {

			System.out.println("Error occured Exception : " + e.toString());
		}

		return project;
	}

	/**
	 * Generate a managed bean class to the current project. It uses as package
	 * name the javaPackage input params, suffixed of web.controllers :
	 * 
	 * 'javaPackage'.web.controllers.
	 * 
	 * @param javaPackage
	 * @param project
	 * @throws FileNotFoundException
	 */
	private static void generateManagedBeanClass(String javaPackage,
			Project project) {

		try {
			System.out.println("Start generating java class for UI");

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
			javaclass.addAnnotation("ManagedBean");

			// Save the java class in the project
			javaSourcefacet.saveJavaSource(javaclass);

			System.out.println("Java class MBean generated.");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
