package am.ajf.forge.core.generators;

import static am.ajf.forge.lib.ForgeConstants.META_INF_FOLDER_ZIP;
import static am.ajf.forge.lib.ForgeConstants.MODEL_POM_EJB;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CONFIG;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CORE;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_LIB;
import static am.ajf.forge.lib.ForgeConstants.SITE_FOLDER;

import java.io.File;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;

import am.ajf.forge.helpers.ExtractionUtils;
import am.ajf.forge.helpers.ProjectHelper;

public class EjbProjectGenerator {

	private ProjectHelper projectUtils = new ProjectHelper();

	/**
	 * Create AJF Ejb project structure, with corresponding AJF dependencies
	 * 
	 * 
	 * @param globalProjectName
	 *            name of the global ajf solution
	 * @param projectFinalName
	 *            name of the current project final name
	 * @param javaPackage
	 *            name of the project's top level package name
	 * @param projectFactory
	 *            of ajf forge
	 * @param projectType
	 *            type of the current ajf project
	 * @param dir
	 *            directory resource where to create the project
	 * @return project
	 */
	@SuppressWarnings("unchecked")
	public Project generateEjbAjfProject(String globalProjectName,
			String projectFinalName, String javaPackage,
			ProjectFactory projectFactory, String projectType,
			DirectoryResource dir) throws Exception {

		Project project = null;

		// Create Project
		project = projectFactory
				.createProject(dir, DependencyFacet.class, MetadataFacet.class,
						JavaSourceFacet.class, ResourceFacet.class);

		// Set pom from example pom file
		projectUtils.setPomFromModelFile(project, MODEL_POM_EJB);

		// Set project meta data in pom
		projectUtils.setBasicProjectData(globalProjectName, projectFinalName,
				project);

		// Set the pom parent
		projectUtils.setInternalPomParent(globalProjectName, project);

		// Extract META-INF/beans.xml to generated project
		ResourceFacet rsf = project.getFacet(ResourceFacet.class);
		ExtractionUtils.unzipFile(META_INF_FOLDER_ZIP, rsf.getResourceFolder()
				.getUnderlyingResourceObject());

		// Internal dependencies
		projectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_LIB);
		projectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_CORE);
		projectUtils.addInternalDependencyScoped(globalProjectName, project,
				PROJECT_TYPE_CONFIG, "runtime");

		// Site
		ExtractionUtils.unzipFile(SITE_FOLDER, new File(project
				.getProjectRoot().getUnderlyingResourceObject()
				.getAbsolutePath().concat("/src")));

		// Generate site.xml file
		projectUtils.generateSiteXmlFile(project, globalProjectName, true,
				false);

		return project;

	}
}
