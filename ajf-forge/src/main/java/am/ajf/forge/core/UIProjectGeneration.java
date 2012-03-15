package am.ajf.forge.core;

import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CONFIG;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CORE;
import static am.ajf.forge.lib.ForgeConstants.STANDARD_PARENT_ARTIFACTID;
import static am.ajf.forge.lib.ForgeConstants.STANDARD_PARENT_GROUPID;
import static am.ajf.forge.lib.ForgeConstants.STANDARD_PARENT_VERSION;
import static am.ajf.forge.lib.ForgeConstants.UI_MAIN_RESOURCES;
import static am.ajf.forge.lib.ForgeConstants.UI_TEST_RESOURCES;
import static am.ajf.forge.lib.ForgeConstants.WEBAPP_ZIP_RESOURCES;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Singleton;

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

@Singleton
public class UIProjectGeneration {

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

	public Project generateProjectUI(String globalProjectName,
			String projectFinalName, String javaPackage,
			ProjectFactory projectFactory, String projectType,
			DirectoryResource dir, boolean isCompact) {

		Project project = null;
		try {
			/*
			 * Generate Project
			 */
			project = generateProject(globalProjectName, projectFinalName,
					projectFactory, dir, isCompact);

			/*
			 * Create an empty java Managed Bean class
			 */
			generateManagedBeanClass(javaPackage, project);

			/*
			 * WEB part
			 */
			System.out.println("** START - WEB PART");

			// Create webapp/Webinf directories
			File webAppDir = ProjectUtils.generateWebAppDirectory(project);
			System.out.println("-- DEBUG : webappDir = "
					+ webAppDir.getAbsolutePath());

			// Extract WebApp resources (from zip)
			System.out.println("** START - Extracting web resources...");
			UIProjectUtils.unzipFile(WEBAPP_ZIP_RESOURCES, webAppDir);
			System.out.println("** END - Web resources extracted");

			System.out.println("** END - WEB PART");

			// Extract from zip all the maven dependencies for generated project
			System.out.println("** START - Adding dependencies...");
			UIProjectUtils.setUIDependencies(project);
			System.out.println("** END - Dependencies added.");

			// Generate the embedded tomcat maven plugin in the generated
			// project's pom.xlm
			System.out.println("** START - Create TOMCAT Plugin");
			UIProjectUtils.setTomCatPlugin(project);
			System.out.println("** END - TOMCAT Plugin generated.");

			/*
			 * PROFILES TOMCAT7 and WAS7
			 */
			System.out.println("** START - Adding maven profiles");
			UIProjectUtils.setProfiles(project);
			System.out.println("** END - Maven profiles added");

			// Unzip Resources to generated project project
			extractResources(project);

		} catch (Exception e) {

			System.out.println("Error occured Exception : " + e.toString());
		}

		return project;
	}

	/**
	 * Generate the UI ajf i accordance with ajf rules. If the current UI
	 * project is part of an exploded ajf project, the internal parent is set,
	 * and the internal dependecies are set to it's pom.xml. If the current UI
	 * project is a stand alone compacted project, only the standard parent will
	 * be set to it's pom.xml. This is what the input boolean parameter
	 * 'isCompact' used for.
	 * 
	 * @param globalProjectName
	 * @param projectFinalName
	 * @param projectFactory
	 * @param dir
	 * @param isCompact
	 * @return Project
	 */
	@SuppressWarnings("unchecked")
	private Project generateProject(String globalProjectName,
			String projectFinalName, ProjectFactory projectFactory,
			DirectoryResource dir, boolean isCompact) {

		// Create Project

		Project project = projectFactory.createProject(dir,
				DependencyFacet.class, MetadataFacet.class,
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
			ProjectUtils.setInternalPomParent(globalProjectName, project);
			/*
			 * Set internal dependencies linked to other project of the
			 * AJF-solution
			 */
			ProjectUtils.addInternalDependency(globalProjectName, project,
					PROJECT_TYPE_CONFIG);
			ProjectUtils.addInternalDependency(globalProjectName, project,
					PROJECT_TYPE_CORE);

		} else {

			// Set the pom parent
			ProjectUtils.setPomParent(STANDARD_PARENT_GROUPID,
					STANDARD_PARENT_ARTIFACTID, STANDARD_PARENT_VERSION,
					project);

		}
		return project;
	}

	/**
	 * Extract from ZIP Files, the resources for the generated project. Main
	 * resources and Test resources will be extracted. Zip files names are set
	 * in the forgeConstants class
	 * 
	 * @param project
	 * @param resourceScope
	 * @throws IOException
	 */
	private void extractResources(Project project) throws IOException {

		System.out.println("** START - extracting resources");

		ResourceFacet resourceFacet = project.getFacet(ResourceFacet.class);
		File resourceFolder;

		// MAIN RESOURCES
		resourceFolder = resourceFacet.getTestResourceFolder()
				.getUnderlyingResourceObject();
		System.out.println("Test Resource directory : "
				+ resourceFolder.getAbsolutePath());
		UIProjectUtils.unzipFile(UI_TEST_RESOURCES, resourceFolder);

		// TEST RESOURCES
		resourceFolder = resourceFacet.getResourceFolder()
				.getUnderlyingResourceObject();
		System.out.println("Main Resource directory : "
				+ resourceFolder.getAbsolutePath());
		UIProjectUtils.unzipFile(UI_MAIN_RESOURCES, resourceFolder);

		System.out.println("** END - resources extracted");

		resourceFolder = null;

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
	private void generateManagedBeanClass(String javaPackage, Project project) {

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

			javaclass.addImport("javax.faces.bean.ManagedBean");

			// Save the java class in the project
			javaSourcefacet.saveJavaSource(javaclass);

			System.out.println("Java class MBean generated.");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
