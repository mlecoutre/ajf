package am.ajf.forge.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.maven.model.Dependency;
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
import am.ajf.forge.util.UIProjectUtils;

public class UIProjectGeneration {

	// private static final Logger logger = LoggerFactory
	// .getLogger(UIProjectGeneration.class);

	/**
	 * Generate AJF UI Project. The isCompact input param determine if this
	 * current UI project will be part of an exploded AJF project (false) or a
	 * compact ajf-project
	 * 
	 * @param globalProjectName
	 * @param projectFinalName
	 * @param javaPackage
	 * @param projectFactory
	 * @param projectType
	 * @param dir
	 * @param isCompact
	 * @return project object
	 */
	@SuppressWarnings("unchecked")
	public static Project generateProjectUI(String globalProjectName,
			String projectFinalName, String javaPackage,
			ProjectFactory projectFactory, String projectType,
			DirectoryResource dir, boolean isCompact) {

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

		// This part is done only when an exploded ajf project is beeing
		// generated
		if (!isCompact) {
			// Set the pom parent
			ProjectUtils.setPomParent(globalProjectName, project);
			/*
			 * Set internal dependencies linked to other project of the
			 * AJF-solution
			 */
			ProjectUtils.addInternalDependency(globalProjectName, project,
					ProjectUtils.PROJECT_TYPE_CONFIG);
			ProjectUtils.addInternalDependency(globalProjectName, project,
					ProjectUtils.PROJECT_TYPE_CORE);
		}

		/*
		 * Create a java class with a method
		 */
		// generateManagedBeanClass(javaPackage, project);

		try {
			/*
			 * WEB part
			 */
			System.out.println("** START - WEB PART");
			// Create webapp/Webinf directories
			File webAppDir = ProjectUtils.generateWebAppDirectory(project);

			System.out.println("-- DEBUG : webappDir = "
					+ webAppDir.getAbsolutePath());

			// Unzip webapp resources
			UIProjectUtils.unzipFile("UIResources.zip", webAppDir);

			/*
			 * Maven Dependencies TODO: use an XML conf file
			 */
			System.out.println("** START - Adding dependencies...");
			UIProjectUtils.setUIDependencies(project);

			System.out.println("** Dependencies added.");

			System.out.println("** START - Create TOMCAT Plugin");
			UIProjectUtils.setTomCatPlugin(project);
			System.out.println("** END - Create TOMCAT Plugin");

			// Unzip test resources to project
			System.out.println("** START - extracting test resources");
			ResourceFacet resource = project.getFacet(ResourceFacet.class);
			File resourceFolder = resource.getTestResourceFolder()
					.getUnderlyingResourceObject();
			System.out.println("Test Resource directory : "
					+ resourceFolder.getAbsolutePath());
			UIProjectUtils.unzipFile("UIResourcesTest.zip", resourceFolder);
			System.out.println("** END - Test resources extracted");

			System.out.println("** END - WEB PART");

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
