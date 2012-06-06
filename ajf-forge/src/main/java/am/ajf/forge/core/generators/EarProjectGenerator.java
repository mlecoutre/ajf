package am.ajf.forge.core.generators;

import static am.ajf.forge.lib.ForgeConstants.MODEL_POM_EAR;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CONFIG;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CORE;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_EJB;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_LIB;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_UI;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_WS;
import static am.ajf.forge.lib.ForgeConstants.SITE_FOLDER;

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

import am.ajf.forge.util.ExtractionUtils;
import am.ajf.forge.util.ProjectHelper;

/**
 * AJF2 Ear project generator.
 * 
 * @author E019851
 * 
 */
@Singleton
public class EarProjectGenerator {

	private ProjectHelper projectUtils = new ProjectHelper();

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
			String projectFinalName, DirectoryResource dir, boolean isWs,
			boolean isEjb) throws Exception {
		Project project;

		project = projectFactory.createProject(dir, DependencyFacet.class,
				MetadataFacet.class, ResourceFacet.class);

		// Set pom from model
		projectUtils.setPomFromModelFile(project, MODEL_POM_EAR);

		projectUtils.setBasicProjectData(globalProjectName, projectFinalName,
				project);
		PackagingFacet packaging = project.getFacet(PackagingFacet.class);
		packaging.setPackagingType(PackagingType.JAR);

		// Set the Pom parent
		projectUtils.setInternalPomParent(globalProjectName, project);

		// Set inter-dependencies
		if (isWs)
			projectUtils.addInternalDependency(globalProjectName, project,
					PROJECT_TYPE_WS);
		if (isEjb)
			projectUtils.addInternalDependency(globalProjectName, project,
					PROJECT_TYPE_EJB);

		projectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_UI);
		projectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_CORE);
		projectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_LIB);
		projectUtils.addInternalDependency(globalProjectName, project,
				PROJECT_TYPE_CONFIG);

		// Configure Ear maven plugin, Web Modules
		MavenCoreFacet mavenFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mavenFacet.getPOM();
		pom.addProperty("project.ws",
				projectFinalName.concat("-").concat(PROJECT_TYPE_WS));
		pom.addProperty("project.ui",
				projectFinalName.concat("-").concat(PROJECT_TYPE_UI));
		mavenFacet.setPOM(pom);

		// Site
		ExtractionUtils.unzipFile(SITE_FOLDER, new File(project
				.getProjectRoot().getUnderlyingResourceObject()
				.getAbsolutePath().concat("/src")));

		// Generate site.xml file
		projectUtils.generateSiteXmlFile(project, globalProjectName, false,
				false);

		return project;
	}
}
