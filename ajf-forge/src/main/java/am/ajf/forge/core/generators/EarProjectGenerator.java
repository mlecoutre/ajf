package am.ajf.forge.core.generators;

import static am.ajf.forge.lib.ForgeConstants.MODEL_POM_EAR;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CONFIG;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CORE;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_LIB;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_UI;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_WS;

import java.io.File;

import javax.inject.Singleton;

import org.apache.maven.model.Model;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;

import am.ajf.forge.util.ProjectUtils;

/**
 * AJF2 Ear project generator.
 * 
 * @author E019851
 * 
 */
@Singleton
public class EarProjectGenerator {

	/**
	 * Construction of an EAR core type project
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
	public Project generateProjectEar(String globalProjectName,
			String javaPackage, ProjectFactory projectFactory,
			String projectFinalName, DirectoryResource dir) throws Exception {
		Project project;

		project = projectFactory.createProject(dir, DependencyFacet.class,
				MetadataFacet.class, ResourceFacet.class);

		// Set pom from model
		ProjectUtils.setPomFromModelFile(project, MODEL_POM_EAR);

		ProjectUtils.setBasicProjectData(globalProjectName, projectFinalName,
				project);
		PackagingFacet packaging = project.getFacet(PackagingFacet.class);
		packaging.setPackagingType(PackagingType.JAR);

		// Remove the Resource/Test folder of ear project
		String resourceTestPath = project.getFacet(ResourceFacet.class)
				.getTestResourceFolder().getParent().getFullyQualifiedName();
		File resourceTestFolder = new File(resourceTestPath);
		if (resourceTestFolder.exists()) {
			resourceTestFolder.delete();
		}

		// Set the Pom parent
		ProjectUtils.setInternalPomParent(globalProjectName, project);

		// Set inter-dependencies
		ProjectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_WS);
		ProjectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_UI);
		ProjectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_CORE);
		ProjectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_LIB);
		ProjectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_CONFIG);

		// Configure Ear maven plugin, Web Modules
		MavenCoreFacet mavenFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mavenFacet.getPOM();
		pom.addProperty("project.ws",
				projectFinalName.concat("-").concat(PROJECT_TYPE_WS));
		pom.addProperty("project.ui",
				projectFinalName.concat("-").concat(PROJECT_TYPE_UI));
		mavenFacet.setPOM(pom);

		return project;
	}
}
