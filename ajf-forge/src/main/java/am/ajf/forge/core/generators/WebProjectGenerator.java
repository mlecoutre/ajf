package am.ajf.forge.core.generators;

import static am.ajf.forge.lib.ForgeConstants.MODEL_POM_UI;
import static am.ajf.forge.lib.ForgeConstants.MODEL_POM_UI_COMPACT;
import static am.ajf.forge.lib.ForgeConstants.MODEL_POM_WS;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CONFIG;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_CORE;
import static am.ajf.forge.lib.ForgeConstants.PROJECT_TYPE_LIB;
import static am.ajf.forge.lib.ForgeConstants.SITE_FOLDER;
import static am.ajf.forge.lib.ForgeConstants.UI_MAIN_RESOURCES;
import static am.ajf.forge.lib.ForgeConstants.UI_TEST_RESOURCES;
import static am.ajf.forge.lib.ForgeConstants.WEBAPP_ZIP_RESOURCES;
import static am.ajf.forge.lib.ForgeConstants.WEBAPP_ZIP_RESOURCES_WS;

import java.io.File;
import java.io.IOException;

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

import am.ajf.forge.utils.ExtractionUtils;
import am.ajf.forge.utils.ProjectHelper;
import am.ajf.forge.utils.UIProjectUtils;

/**
 * AJF2 web project generator. This class can be used for a WS typed project or
 * an UI typed project. The UI project can be part of an exploded solution, or a
 * compacted war AJF2 project.
 * 
 * @author E019851
 * 
 */
@Singleton
public class WebProjectGenerator {

	private ProjectHelper projectUtils = new ProjectHelper();

	/**
	 * Generate AJF WS WEB Project.
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
	public Project generateWSAjfProject(String globalProjectName,
			String projectFinalName, String javaPackage,
			ProjectFactory projectFactory, String projectType,
			DirectoryResource dir) {

		Project project = null;

		try {
			/*
			 * Generate Project
			 */
			project = generateProject(globalProjectName, projectFinalName,
					projectFactory, dir, false, MODEL_POM_WS);

			extractWebResources(project, WEBAPP_ZIP_RESOURCES_WS);

			// Set the Pom parent
			projectUtils.setInternalPomParent(globalProjectName, project);

			// Set project meta data in pom
			projectUtils.setBasicProjectData(globalProjectName,
					projectFinalName, project);

			// Site
			ExtractionUtils.unzipFile(SITE_FOLDER, new File(project
					.getProjectRoot().getUnderlyingResourceObject()
					.getAbsolutePath().concat("/src")));

			// Generate site.xml file
			projectUtils.generateSiteXmlFile(project, globalProjectName, true,
					false);

		} catch (Exception e) {

			System.err
					.println("Error occured during ajf web-WS project generation : "
							+ e.toString());
		}

		return project;

	}

	/**
	 * Generate AJF UI WEB Project. The isCompact input boolean param determine
	 * if this current UI project will be part of an exploded AJF project
	 * (false) or a compact ajf-project
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
	 * @param isCompact
	 *            true if the UI project is a Compacted AJF solution
	 * @return project
	 */
	public Project generateUIAjfProject(String globalProjectName,
			String projectFinalName, String javaPackage,
			ProjectFactory projectFactory, String projectType,
			DirectoryResource dir, boolean isCompact) {

		Project project = null;
		try {
			/*
			 * Generate Project
			 */
			if (isCompact) {
				project = generateProject(globalProjectName, projectFinalName,
						projectFactory, dir, isCompact, MODEL_POM_UI_COMPACT);
			} else {
				project = generateProject(globalProjectName, projectFinalName,
						projectFactory, dir, isCompact, MODEL_POM_UI);

			}

			extractWebResources(project, WEBAPP_ZIP_RESOURCES);
			generateUIWebResources(javaPackage, project, isCompact);

			if (isCompact) {
				// Extract the persistence.xml model file to the META-INF folder
				// of the current project
				ExtractionUtils.extractPersistenceXmlFile(project);

			} else {
				// Set the Pom parent
				projectUtils.setInternalPomParent(globalProjectName, project);
			}

			// Set project meta data in pom
			projectUtils.setBasicProjectData(globalProjectName,
					projectFinalName, project);

			// Site
			ExtractionUtils.unzipFile(SITE_FOLDER, new File(project
					.getProjectRoot().getUnderlyingResourceObject()
					.getAbsolutePath().concat("/src")));

		} catch (Exception e) {

			System.err
					.println("Error occured during ajf web-UI project generation : "
							+ e.toString());
		}

		return project;
	}

	/**
	 * Generate WebApp Directory with web resources. This must bne done only for
	 * an AJF2 UI typed project
	 * 
	 * @param javaPackage
	 *            top level java package name
	 * @param project
	 * @throws Exception
	 * @Return WEPAPP directory
	 */
	private File generateUIWebResources(String javaPackage, Project project,
			boolean isCompact) throws Exception {

		System.out.println("** START - WEB PART");

		/*
		 * Create an empty java Managed Bean class
		 */
		// UIProjectUtils.generateManagedBeanClass(javaPackage, project);

		File webappDir = extractWebResources(project, WEBAPP_ZIP_RESOURCES);

		// Unzip Resources (main resources and test resources) to
		// generated project project
		extractResources(project);

		System.out.println("** END - WEB PART");

		return webappDir;
	}

	/**
	 * Extract webapp folder from resource zip file
	 * 
	 * @param project
	 * @param webAppZipResource
	 * @throws IOException
	 */
	private File extractWebResources(Project project, String webAppZipResource)
			throws IOException {

		// Create webapp/Webinf directories
		File webAppDir = UIProjectUtils.generateWebAppDirectory(project);
		System.out.println("-- DEBUG : webappDir = "
				+ webAppDir.getAbsolutePath());

		// Extract WebApp resources (from zip)
		System.out.println("** START - Extracting web resources...");
		ExtractionUtils.unzipFile(webAppZipResource, webAppDir);
		System.out.println("** END - Web resources extracted");

		return webAppDir;
	}

	/**
	 * Generate the web ajf in accordance with ajf rules. If the current web
	 * project is part of an exploded ajf project, the internal parent, and the
	 * inter-dependencies are set to it's pom.xml. If the current UI project is
	 * a stand alone compacted project, only the standard parent will be set to
	 * it's pom.xml. This is what the input boolean parameter 'isCompact' used
	 * for.
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
			DirectoryResource dir, boolean isCompact, String modelPomFile)
			throws Exception {

		// Create Project
		Project project = projectFactory.createProject(dir,
				DependencyFacet.class, MetadataFacet.class,
				JavaSourceFacet.class, ResourceFacet.class);

		projectUtils.setPomFromModelFile(project, modelPomFile);

		// Set project packaging
		PackagingFacet packaging = project.getFacet(PackagingFacet.class);
		packaging.setPackagingType(PackagingType.WAR);

		// Set name of the project
		packaging.setFinalName(projectFinalName);

		// This part is done only when an exploded ajf project is beeing
		// generated
		if (!isCompact) {

			// Set the pom parent
			projectUtils.setInternalPomParent(globalProjectName, project);

			// Set internal dependencies linked to other project of the
			// AJF-solution
			projectUtils.addInternalDependencyScoped(globalProjectName,
					project, PROJECT_TYPE_CONFIG, "runtime");

			projectUtils.addInternalDependency(globalProjectName, project,
					PROJECT_TYPE_LIB);

			projectUtils.addInternalDependencyScoped(globalProjectName,
					project, PROJECT_TYPE_CORE, "runtime");

		}

		return project;
	}

	/**
	 * Extract from ZIP Files, the resources for the generated project: Main
	 * resources and Test resources
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
		ExtractionUtils.unzipFile(UI_TEST_RESOURCES, resourceFolder);

		// TEST RESOURCES
		resourceFolder = resourceFacet.getResourceFolder()
				.getUnderlyingResourceObject();
		System.out.println("Main Resource directory : "
				+ resourceFolder.getAbsolutePath());
		ExtractionUtils.unzipFile(UI_MAIN_RESOURCES, resourceFolder);

		System.out.println("** END - resources extracted");

		resourceFolder = null;

	}

}
