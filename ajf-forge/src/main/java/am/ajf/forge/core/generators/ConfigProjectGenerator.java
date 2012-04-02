package am.ajf.forge.core.generators;

import static am.ajf.forge.lib.ForgeConstants.MODEL_POM_CONFIG;
import static am.ajf.forge.lib.ForgeConstants.SITE_FOLDER;

import java.io.File;
import java.io.IOException;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;

import am.ajf.forge.util.ExtractionUtils;
import am.ajf.forge.util.ProjectUtils;

public class ConfigProjectGenerator {

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
		System.out.println("Creating config project");

		project = projectFactory.createProject(dir, DependencyFacet.class,
				MetadataFacet.class, ResourceFacet.class);

		// Set pom from model
		ProjectUtils.setPomFromModelFile(project, MODEL_POM_CONFIG);

		ProjectUtils.setBasicProjectData(globalProjectName, projectFinalName,
				project);

		PackagingFacet packaging = project.getFacet(PackagingFacet.class);
		packaging.setPackagingType(PackagingType.JAR);

		String resourcesFolder = project.getFacet(ResourceFacet.class)
				.getResourceFolder().getFullyQualifiedName();

		System.out.println("--> Config resource path : " + resourcesFolder);

		// Create META-INF Folder
		File metainfFolder = new File(resourcesFolder + "/META-INF");
		metainfFolder.mkdir();

		/*
		 * Create Persistence.xml
		 */
		File persistenceFile = new File(metainfFolder.getAbsolutePath()
				+ "/persistence.xml");
		try {
			persistenceFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		persistenceFile = null;

		/*
		 * Create logback.xml
		 */
		File logbackFile = new File(resourcesFolder + "/logback.xml");

		try {
			logbackFile.createNewFile();
		} catch (IOException e) {
			System.out.println("** ERROR ");
		}
		logbackFile = null;

		/*
		 * Set the Pom parent
		 */
		ProjectUtils.setInternalPomParent(globalProjectName, project);

		// Site
		ExtractionUtils.unzipFile(SITE_FOLDER, new File(project
				.getProjectRoot().getUnderlyingResourceObject()
				.getAbsolutePath().concat("/src")));

		return project;
	}

}
