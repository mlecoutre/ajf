package am.ajf.forge.core.generators;

import static am.ajf.forge.lib.ForgeConstants.*;
import static am.ajf.forge.lib.ForgeConstants.MODEL_POM_EJB;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CONFIG;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_LIB;

import java.io.File;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;

import am.ajf.forge.util.ExtractionUtils;
import am.ajf.forge.util.ProjectUtils;

public class EjbProjectGenerator {

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

		// Remove the Test folder of the project
		String resourceTestPath = project.getFacet(ResourceFacet.class)
				.getTestResourceFolder().getParent().getFullyQualifiedName();
		File resourceTestFolder = new File(resourceTestPath);
		if (resourceTestFolder.exists()) {
			resourceTestFolder.delete();
		}

		// Set pom from example pom file
		ProjectUtils.setPomFromModelFile(project, MODEL_POM_EJB);

		// Set project meta data in pom
		ProjectUtils.setBasicProjectData(globalProjectName, projectFinalName,
				project);

		// Set the pom parent
		ProjectUtils.setInternalPomParent(globalProjectName, project);

		// Extract META-INF/beans.xml to generated project
		ResourceFacet rsf = project.getFacet(ResourceFacet.class);
		ExtractionUtils.unzipFile(META_INF_FOLDER_ZIP, rsf.getResourceFolder()
				.getUnderlyingResourceObject());

		// Internal dependencies
		ProjectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_LIB);
		ProjectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_CORE);
		ProjectUtils.addInternalDependencyScoped(globalProjectName, project,
				PROJECT_TYPE_CONFIG, "runtime");

		// Site
		ExtractionUtils.unzipFile(SITE_FOLDER, new File(project
				.getProjectRoot().getUnderlyingResourceObject()
				.getAbsolutePath().concat("/src")));

		// Generate site.xml file
		ProjectUtils.generateSiteXmlFile(project, globalProjectName, true,
				false);

		return project;

	}
}
