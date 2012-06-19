package am.ajf.forge.core.generators;

import static am.ajf.forge.lib.ForgeConstants.MODEL_POM_CONFIG;
import static am.ajf.forge.lib.ForgeConstants.SITE_FOLDER;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;

import am.ajf.forge.utils.ExtractionUtils;
import am.ajf.forge.utils.ProjectHelper;

/**
 * AJF2 Config project generator.
 * 
 * @author E019851
 * 
 */
@Singleton
public class ConfigProjectGenerator {

	private ProjectHelper projectUtils = new ProjectHelper();

	/**
	 * Construction of an AJF Config type project
	 * 
	 * @param globalProjectName
	 * @param javaPackage
	 * @param projectFactory
	 * @param projectFinalName
	 * @param dir
	 * @return Project object corresponding to the generated project
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Project generateProjectConfig(String globalProjectName,
			String javaPackage, ProjectFactory projectFactory,
			String projectFinalName, DirectoryResource dir) throws Exception {

		Project project;
		System.out.println("Creating config project...");

		project = projectFactory.createProject(dir, DependencyFacet.class,
				MetadataFacet.class, ResourceFacet.class);

		// Set pom from model
		projectUtils.setPomFromModelFile(project, MODEL_POM_CONFIG);

		// Set project meta data
		projectUtils.setBasicProjectData(globalProjectName, projectFinalName,
				project);

		// Set packaging
		PackagingFacet packaging = project.getFacet(PackagingFacet.class);
		packaging.setPackagingType(PackagingType.JAR);

		// Retrieve Resource folder of the current project
		String resourcesFolder = project.getFacet(ResourceFacet.class)
				.getResourceFolder().getFullyQualifiedName();
		System.out.println("**DEBUG: Config resource directory : "
				+ resourcesFolder);

		// Create META-INF Folder in Resource
		File metainfFolder = new File(resourcesFolder + "/META-INF");
		metainfFolder.mkdir();

		// Extract persistence.xml file
		ExtractionUtils.extractPersistenceXmlFile(project);

		// Create logback.xml
		createLogBackFile(resourcesFolder);

		/*
		 * Set the Pom parent
		 */
		projectUtils.setInternalPomParent(globalProjectName, project);

		// Site
		ExtractionUtils.unzipFile(SITE_FOLDER, new File(project
				.getProjectRoot().getUnderlyingResourceObject()
				.getAbsolutePath().concat("/src")));

		// Generate site.xml file
		projectUtils.generateSiteXmlFile(project, globalProjectName, false,
				false);

		return project;
	}

	/**
	 * Create a logback.xml file in the project's resourcess
	 * 
	 * @param resourcesFolder
	 */
	private void createLogBackFile(String resourcesFolder) {
		File logbackFile = new File(resourcesFolder + "/logback.xml");

		try {
			logbackFile.createNewFile();
		} catch (IOException e) {
			System.err
					.println("** ERROR : Could not generate logback.xml file for ajf-config project");
		}
		logbackFile = null;
	}

}
