package am.ajf.forge.core;

import static am.ajf.forge.lib.ForgeConstants.AJF_CORE;
import static am.ajf.forge.lib.ForgeConstants.AJF_DEPS_MODEL_FILE;
import static am.ajf.forge.lib.ForgeConstants.AJF_INJECTION;
import static am.ajf.forge.lib.ForgeConstants.AJF_MONITORING;
import static am.ajf.forge.lib.ForgeConstants.AJF_PERSISTENCE;
import static am.ajf.forge.lib.ForgeConstants.AJF_REMOTING;
import static am.ajf.forge.lib.ForgeConstants.AJF_TESTING;
import static am.ajf.forge.lib.ForgeConstants.META_INF_FOLDER_ZIP;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CONFIG;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.apache.maven.model.Model;
import org.jboss.forge.maven.MavenCoreFacet;
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
public class LibProjectGeneration {

	/**
	 * Create AJF core project structure, with corresponding AJF dependencies
	 * 
	 * 
	 * @param globalProjectName
	 * @param projectFinalName
	 * @param javaPackage
	 * @param projectFactory
	 * @param projectType
	 * @param dir
	 * @param isCompact
	 * @return
	 * @throws Exception
	 */
	public Project generateLibAjfProject(String globalProjectName,
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

			List<String> ajfDependencies = new ArrayList<String>();

			ajfDependencies.add(AJF_CORE);
			ajfDependencies.add(AJF_INJECTION);
			ajfDependencies.add(AJF_PERSISTENCE);
			ajfDependencies.add(AJF_REMOTING);
			ajfDependencies.add(AJF_TESTING);
			ajfDependencies.add(AJF_MONITORING);

			// Get the Pom
			MavenCoreFacet mavenCoreFacet = project
					.getFacet(MavenCoreFacet.class);
			Model pom = mavenCoreFacet.getPOM();

			ProjectUtils.addAjfDependenciesToPom(ajfDependencies,
					AJF_DEPS_MODEL_FILE, pom);

			mavenCoreFacet.setPOM(pom);

		} catch (Exception e) {

			System.err
					.println("** ERROR occured during ajf lib project generation : "
							+ e.toString());
			throw e;
		}

		return project;
	}

	/**
	 * Generate the LIB ajf project in accordance with ajf rules.
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
		ProjectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_CONFIG);

		/*
		 * Extract META-INF/beans.xml to generated project
		 */
		ResourceFacet rsf = project.getFacet(ResourceFacet.class);
		UIProjectUtils.unzipFile(META_INF_FOLDER_ZIP, rsf.getResourceFolder()
				.getUnderlyingResourceObject());

		return project;
	}
}
