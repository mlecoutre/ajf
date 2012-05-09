package am.ajf.forge.core.generators;

import static am.ajf.forge.lib.ForgeConstants.MODEL_POM_PARENT;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CONFIG;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CORE;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_EAR;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_EJB;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_LIB;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_UI;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_WS;
import static am.ajf.forge.lib.ForgeConstants.SITE_FOLDER;
import static am.ajf.forge.lib.ForgeConstants.START_PROJECT_MILESTONE;

import java.io.File;

import javax.inject.Singleton;

import org.apache.maven.model.Model;
import org.apache.maven.model.Scm;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;

import am.ajf.forge.util.ExtractionUtils;
import am.ajf.forge.util.ProjectUtils;

/**
 * AJF2 Parent project generator. (parent of the current solution)
 * 
 * @author E019851
 * 
 */
@Singleton
public class ParentProjectGenerator {

	/**
	 * Construction of a PARENT type project
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
	public Project generateProjectParent(String globalProjectName,
			String javaPackage, ProjectFactory projectFactory,
			String projectFinalName, DirectoryResource dir, boolean isWs,
			boolean isEjb) throws Exception {

		Project project;
		project = projectFactory.createProject(dir, DependencyFacet.class,
				MetadataFacet.class);

		// Set pom from example pom file
		ProjectUtils.setPomFromModelFile(project, MODEL_POM_PARENT);

		// Set project meta data
		ProjectUtils.setBasicProjectData(globalProjectName, projectFinalName,
				project);

		PackagingFacet packaging = project.getFacet(PackagingFacet.class);
		packaging.setPackagingType(PackagingType.BASIC);

		String projectGroupId = ProjectUtils.projectGroupId(globalProjectName);

		ProjectUtils.addManagementDependency(project, projectGroupId,
				globalProjectName + "-" + PROJECT_TYPE_CONFIG,
				START_PROJECT_MILESTONE, null);
		ProjectUtils.addManagementDependency(project, projectGroupId,
				globalProjectName + "-" + PROJECT_TYPE_LIB,
				START_PROJECT_MILESTONE, null);
		ProjectUtils.addManagementDependency(project, projectGroupId,
				globalProjectName + "-" + PROJECT_TYPE_CORE,
				START_PROJECT_MILESTONE, null);
		ProjectUtils.addManagementDependency(project, projectGroupId,
				globalProjectName + "-" + PROJECT_TYPE_UI,
				START_PROJECT_MILESTONE, "war");
		if (isWs)
			ProjectUtils.addManagementDependency(project, projectGroupId,
					globalProjectName + "-" + PROJECT_TYPE_WS,
					START_PROJECT_MILESTONE, "war");
		if (isEjb)
			ProjectUtils.addManagementDependency(project, projectGroupId,
					globalProjectName + "-" + PROJECT_TYPE_EJB,
					START_PROJECT_MILESTONE, "ejb");

		// Get the MavenFacet in order to grab the pom
		MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mavenCoreFacet.getPOM();

		// Add the children modules
		pom.addModule("../" + globalProjectName + "-" + PROJECT_TYPE_CONFIG);
		pom.addModule("../" + globalProjectName + "-" + PROJECT_TYPE_LIB);
		pom.addModule("../" + globalProjectName + "-" + PROJECT_TYPE_CORE);
		pom.addModule("../" + globalProjectName + "-" + PROJECT_TYPE_UI);
		pom.addModule("../" + globalProjectName + "-" + PROJECT_TYPE_EAR);

		if (isWs)
			pom.addModule("../" + globalProjectName + "-" + PROJECT_TYPE_WS);

		if (isEjb)
			pom.addModule("../" + globalProjectName + "-" + PROJECT_TYPE_EJB);

		// Set SCM Connection

		Scm scm = new Scm();
		scm.setConnection("scm:svn:http://web-svn-srv/repos/ITSWE/trunk/"
				+ globalProjectName + "/" + projectFinalName);
		scm.setDeveloperConnection("scm:svn:http://web-svn-srv/repos/ITSWE/trunk/"
				+ globalProjectName + "/" + projectFinalName);
		scm.setUrl("http://web-svn-viewer/listing.php?repname=ITSWE&amp;path=/trunk/"
				+ globalProjectName + "/" + projectFinalName);

		pom.setScm(scm);
		mavenCoreFacet.setPOM(pom);

		// Site
		ExtractionUtils.unzipFile(SITE_FOLDER, new File(project
				.getProjectRoot().getUnderlyingResourceObject()
				.getAbsolutePath().concat("/src")));

		// Generate site.xml file
		ProjectUtils
				.generateSiteXmlFile(project, globalProjectName, true, true);

		return project;
	}

}
