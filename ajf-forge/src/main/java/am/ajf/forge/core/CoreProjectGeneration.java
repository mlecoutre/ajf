package am.ajf.forge.core;

import static am.ajf.forge.lib.ForgeConstants.META_INF_FOLDER_ZIP;
import static am.ajf.forge.lib.ForgeConstants.MODEL_POM_CORE;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CONFIG;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_LIB;

import javax.inject.Singleton;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;

import am.ajf.forge.util.ExtractionUtils;
import am.ajf.forge.util.ProjectUtils;

/**
 * 
 * @author E019851
 * 
 */
@Singleton
public class CoreProjectGeneration {

	/**
	 * Create AJF core project structure, with corresponding AJF dependencies
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
	public Project generateCoreAjfProject(String globalProjectName,
			String projectFinalName, String javaPackage,
			ProjectFactory projectFactory, String projectType,
			DirectoryResource dir) throws Exception {

		Project project = null;
		try {
			/*
			 * Generate Project
			 */
			project = generateProject(globalProjectName, projectFinalName,
					projectFactory, dir);

			// Set pom from example pom file
			ProjectUtils.setPomFromModelFile(project, MODEL_POM_CORE);

			// Set the Pom parent
			ProjectUtils.setInternalPomParent(globalProjectName, project);

			// Set project meta data in pom
			ProjectUtils.setBasicProjectData(globalProjectName,
					projectFinalName, project);

		} catch (Exception e) {

			System.err
					.println("** ERROR occured during ajf core project generation : "
							+ e.toString());
			throw e;
		}

		return project;
	}

	/**
	 * Generate the CORE ajf project in accordance with ajf rules.
	 * 
	 * @param globalProjectName
	 * @param projectFinalName
	 * @param projectFactory
	 * @param dir
	 * @param isCompact
	 * @return Project
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Project generateProject(String globalProjectName,
			String projectFinalName, ProjectFactory projectFactory,
			DirectoryResource dir) throws Exception {

		// Create Project
		Project project = projectFactory.createProject(dir,
				DependencyFacet.class, MetadataFacet.class,
				JavaSourceFacet.class, ResourceFacet.class);

		// Set project meta data in pom
		ProjectUtils.setBasicProjectData(globalProjectName, projectFinalName,
				project);

		// Set project packaging
		PackagingFacet packaging = project.getFacet(PackagingFacet.class);
		packaging.setPackagingType(PackagingType.JAR);

		// Set name of the project
		packaging.setFinalName(projectFinalName);

		// Set the pom parent
		ProjectUtils.setInternalPomParent(globalProjectName, project);

		/*
		 * Set dependencies
		 */
		ProjectUtils.addInternalDependencyScoped(globalProjectName, project,
				PROJECT_TYPE_CONFIG, "runtime");
		ProjectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_LIB);

		/*
		 * Extract META-INF/beans.xml to generated project
		 */
		ResourceFacet rsf = project.getFacet(ResourceFacet.class);
		ExtractionUtils.unzipFile(META_INF_FOLDER_ZIP, rsf.getResourceFolder()
				.getUnderlyingResourceObject());

		return project;
	}
}
