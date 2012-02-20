package am.ajf.forge.core;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Scm;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;

import am.ajf.forge.util.EclipseUtils;
import am.ajf.forge.util.ProjectUtils;

public class CreateProject {

	private String groupId;

	/**
	 * Creation of an AJF project. This method generate a project in accordance
	 * to the AJF norm, depending on the project type set as input (core,
	 * config, ear, ui...)
	 * 
	 * @param globalProjectName
	 * @param javaPackage
	 * @param projectFolder
	 * @param projectFactory
	 * @param projectType
	 * @param projectFinalName
	 * @throws Exception
	 */
	public void createAjfProject(String globalProjectName, String javaPackage,
			Resource<?> projectFolder, ProjectFactory projectFactory,
			String projectType, String projectFinalName) throws Exception {

		System.out.println("START generating ajf project " + projectType);

		/*
		 * project directory initialization
		 */
		DirectoryResource dir = initializeProjectDirectory(projectFolder);

		/*
		 * Creation of the project depending on the project type
		 */
		Project project = null;

		if (ProjectUtils.PROJECT_TYPE_PARENT.equals(projectType)) {

			project = generateProjectParent(globalProjectName, javaPackage,
					projectFactory, projectFinalName, dir);

		} else if (ProjectUtils.PROJECT_TYPE_CONFIG.equals(projectType)) {

			project = generateProjectConfig(globalProjectName, javaPackage,
					projectFactory, projectFinalName, dir);

		} else if (ProjectUtils.PROJECT_TYPE_EAR.equals(projectType)) {

			project = generateProjectEar(globalProjectName, javaPackage,
					projectFactory, projectFinalName, dir);

		} else if (ProjectUtils.PROJECT_TYPE_CORE.equals(projectType)) {

			project = generateProjectCore(globalProjectName, projectFinalName,
					javaPackage, projectFactory, projectType, dir);

		} else if (ProjectUtils.PROJECT_TYPE_UI.equals(projectType)) {

			project = generateProjectUI(globalProjectName, projectFinalName,
					javaPackage, projectFactory, projectType, dir);

		} else if (ProjectUtils.PROJECT_TYPE_LIB.equals(projectType)) {

			project = generateProjectLib(globalProjectName, projectFinalName,
					javaPackage, projectFactory, projectType, dir);

		} else if (ProjectUtils.PROJECT_TYPE_WS.equals(projectType)) {

			project = generateProjectWS(globalProjectName, projectFinalName,
					javaPackage, projectFactory, projectType, dir);
		} else {

			String errorMessage = "Unknown project type : ".concat(projectType);
			System.out.println(errorMessage);
			throw new Exception(errorMessage);

		}

		// Get the root directory of the current project
		String projectRootDirectory = project.getProjectRoot()
				.getFullyQualifiedName();

		/*
		 * Generate .project file of the project for it to be importable in
		 * eclipse
		 */
		generateEclipseProjectFile(projectFinalName, projectRootDirectory);

		/*
		 * Generate the MAVEN PREFS file
		 */
		generateMavenPrefsFile(projectRootDirectory);

		/*
		 * generate classPath file
		 */
		EclipseUtils.generateClassPathFile(projectRootDirectory, projectType);

		/*
		 * End log
		 */
		System.out.println("Project".concat(globalProjectName + "-"
				+ projectType)
				+ " created");

	}

	/**
	 * Generate the .Settings/org.maven.ide.eclipse.prefs file containing the
	 * preferences for maven dealing with the current project
	 * 
	 * @param projectRootDirectory
	 * @throws Exception
	 */
	private void generateMavenPrefsFile(String projectRootDirectory)
			throws Exception {
		try {
			EclipseUtils.generateEclipseMavenPrefFile(projectRootDirectory);
		} catch (Exception e) {
			throw new Exception(
					"Error occured during the generation of the MAVEN preferences file, containing the preferences dealing with maven such as the profile.");
		}
	}

	/**
	 * Generate the .project file needed to import the current project in
	 * Eclipse
	 * 
	 * @param projectFinalName
	 * @param projectRootDirectory
	 * @throws FactoryConfigurationError
	 * @throws Exception
	 */
	private void generateEclipseProjectFile(String projectFinalName,
			String projectRootDirectory) throws FactoryConfigurationError,
			Exception {

		try {
			EclipseUtils.generateEclipseProjectFile(projectFinalName,
					projectRootDirectory);

		} catch (Exception e) {

			// Exception should already be logged
			throw new Exception(
					"Error occured during the generation of the .Project file, which is needed to correctly import the project in Eclipse.",
					e);

		}
	}

	/**
	 * Construction of an PARENT core type project
	 * 
	 * @param globalProjectName
	 * @param javaPackage
	 * @param projectFactory
	 * @param projectFinalName
	 * @param dir
	 * @return Project object corresponding to the generated project
	 */
	@SuppressWarnings("unchecked")
	private Project generateProjectParent(String globalProjectName,
			String javaPackage, ProjectFactory projectFactory,
			String projectFinalName, DirectoryResource dir) {

		Project project;
		project = projectFactory.createProject(dir, DependencyFacet.class,
				MetadataFacet.class);

		setBasicProjectData(globalProjectName, projectFinalName, project);

		PackagingFacet packaging = project.getFacet(PackagingFacet.class);
		packaging.setPackagingType(PackagingType.BASIC);

		// Get the MavenFacet in order to grab the pom
		MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mavenCoreFacet.getPOM();

		// Add the children modules
		pom.addModule("../" + globalProjectName + "-"
				+ ProjectUtils.PROJECT_TYPE_CONFIG);
		pom.addModule("../" + globalProjectName + "-"
				+ ProjectUtils.PROJECT_TYPE_CORE);
		pom.addModule("../" + globalProjectName + "-"
				+ ProjectUtils.PROJECT_TYPE_UI);
		pom.addModule("../" + globalProjectName + "-"
				+ ProjectUtils.PROJECT_TYPE_EAR);

		// Set am/parent
		Parent parent = new Parent();
		parent.setGroupId("am.parent");
		parent.setArtifactId("standard");
		parent.setVersion("2.0.7");

		pom.setParent(parent);

		mavenCoreFacet.setPOM(pom);

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

		return project;
	}

	/**
	 * Construction of an EAR core type project
	 * 
	 * @param globalProjectName
	 * @param javaPackage
	 * @param projectFactory
	 * @param projectFinalName
	 * @param dir
	 * @return Project object corresponding to the generated project
	 */
	@SuppressWarnings("unchecked")
	private Project generateProjectEar(String globalProjectName,
			String javaPackage, ProjectFactory projectFactory,
			String projectFinalName, DirectoryResource dir) {
		Project project;

		project = projectFactory.createProject(dir, DependencyFacet.class,
				MetadataFacet.class, ResourceFacet.class);

		setBasicProjectData(globalProjectName, projectFinalName, project);
		PackagingFacet packaging = project.getFacet(PackagingFacet.class);
		packaging.setPackagingType(PackagingType.JAR);

		/*
		 * Remove the Resource/Test folder of ear project
		 */
		String resourceTestPath = project.getFacet(ResourceFacet.class)
				.getTestResourceFolder().getParent().getFullyQualifiedName();
		File resourceTestFolder = new File(resourceTestPath);
		if (resourceTestFolder.exists()) {
			resourceTestFolder.delete();
		}

		/*
		 * Set the Pom parent
		 */
		setPomParent(globalProjectName, project);

		/*
		 * Set dependencies
		 */
		addDependency(globalProjectName, project, ProjectUtils.PROJECT_TYPE_UI);
		addDependency(globalProjectName, project,
				ProjectUtils.PROJECT_TYPE_CORE);
		addDependency(globalProjectName, project,
				ProjectUtils.PROJECT_TYPE_CONFIG);
		addDependency(globalProjectName, project, ProjectUtils.PROJECT_TYPE_UI);

		return project;
	}

	/**
	 * Construction of an AJF Config type project
	 * 
	 * @param globalProjectName
	 * @param javaPackage
	 * @param projectFactory
	 * @param projectFinalName
	 * @param dir
	 * @return Project object corresponding to the generated project
	 */
	@SuppressWarnings("unchecked")
	private Project generateProjectConfig(String globalProjectName,
			String javaPackage, ProjectFactory projectFactory,
			String projectFinalName, DirectoryResource dir) {

		Project project;
		System.out.println("Creating config project");

		project = projectFactory.createProject(dir, DependencyFacet.class,
				MetadataFacet.class, ResourceFacet.class);

		setBasicProjectData(globalProjectName, projectFinalName, project);

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
		 * Create logback.xlml
		 */
		File logbackFile = new File(resourcesFolder + "/logback.xml");

		try {
			logbackFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logbackFile = null;

		/*
		 * Set the Pom parent
		 */

		setPomParent(globalProjectName, project);
		return project;
	}

	/**
	 * 
	 * Construction of an AJF core type project
	 * 
	 * @param projectFinalName
	 * @param javaPackage
	 * @param projectFactory
	 * @param projectType
	 * @param dir
	 * @return Project object corresponding to the generated project
	 */
	@SuppressWarnings("unchecked")
	private Project generateProjectCore(String globalProjectName,
			String projectFinalName, String javaPackage,
			ProjectFactory projectFactory, String projectType,
			DirectoryResource dir) {

		Project project;
		project = projectFactory
				.createProject(dir, DependencyFacet.class, MetadataFacet.class,
						JavaSourceFacet.class, ResourceFacet.class);

		setBasicProjectData(globalProjectName, projectFinalName, project);

		// Packaging
		PackagingFacet packaging = project.getFacet(PackagingFacet.class);
		packaging.setPackagingType(PackagingType.JAR);

		// Set name of the project
		packaging.setFinalName(projectFinalName);

		System.out.println("--> start generating java source");
		try {
			JavaSourceFacet javaSourcefacet = project
					.getFacet(JavaSourceFacet.class);

			// Create a main with some code in a package
			javaSourcefacet
					.saveJavaSource(JavaParser
							.create(JavaClass.class)
							.setPackage(javaPackage + ".dao")
							.setName("ExempleDAO")
							.addMethod(
									"public static void exempleDaoMethod(String[] args) {}")
							.setBody(
									"System.out.println(\"Hi there! This is an AJF Project "
											+ projectFinalName + " "
											+ projectType + " component.\");")
							.getOrigin());

			System.out.println("--> End generating java source");

		} catch (Exception e) {

			System.out.println("Error occured Exception : " + e.toString());
		}

		// Set the pom parent
		setPomParent(globalProjectName, project);

		/*
		 * Set dependencies
		 */
		addDependency(globalProjectName, project,
				ProjectUtils.PROJECT_TYPE_CONFIG);

		return project;
	}

	/**
	 * Construction of an AJF UI type project
	 * 
	 * @param globalProjectName
	 * @param projectFinalName
	 * @param javaPackage
	 * @param projectFactory
	 * @param projectType
	 * @param dir
	 * @return project object corresponding to the generated project
	 */
	@SuppressWarnings("unchecked")
	private Project generateProjectUI(String globalProjectName,
			String projectFinalName, String javaPackage,
			ProjectFactory projectFactory, String projectType,
			DirectoryResource dir) {

		Project project;
		project = projectFactory
				.createProject(dir, DependencyFacet.class, MetadataFacet.class,
						JavaSourceFacet.class, ResourceFacet.class);

		setBasicProjectData(globalProjectName, projectFinalName, project);

		PackagingFacet packaging = project.getFacet(PackagingFacet.class);
		packaging.setPackagingType(PackagingType.WAR);

		// Set name of the project
		packaging.setFinalName(projectFinalName);

		// Set the pom parent
		setPomParent(globalProjectName, project);

		/*
		 * Set dependencies
		 */
		addDependency(globalProjectName, project,
				ProjectUtils.PROJECT_TYPE_CONFIG);
		addDependency(globalProjectName, project,
				ProjectUtils.PROJECT_TYPE_CORE);

		/*
		 * Create a java class with a method
		 */
		try {

			System.out.println("Start generating java class for UI");

			JavaSourceFacet javaSourcefacet = project
					.getFacet(JavaSourceFacet.class);

			// Create a java class
			JavaClass javaclass = JavaParser
					.create(JavaClass.class)
					.setPackage(javaPackage + ".web.controllers")
					.setName("ExempleMBean")
					.addMethod(
							"public static void exempleMBeanMethod(String[] args) {}")
					.setBody(
							"System.out.println(\"Hi there! This is an AJF Project UI method ")
					.getOrigin();

			// Add the annotation for JSF2 managed bean
			javaclass.addAnnotation("ManagedBean");

			// Save the java class in the project
			javaSourcefacet.saveJavaSource(javaclass);

			System.out.println("--> End generating java source");

		} catch (Exception e) {

			System.out.println("Error occured Exception : " + e.toString());
		}

		return project;
	}

	/**
	 * 
	 * Construction of an AJF LIB type project
	 * 
	 * @param globalProjectName
	 * @param projectFinalName
	 * @param javaPackage
	 * @param projectFactory
	 * @param projectType
	 * @param dir
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Project generateProjectLib(String globalProjectName,
			String projectFinalName, String javaPackage,
			ProjectFactory projectFactory, String projectType,
			DirectoryResource dir) {

		Project project;
		project = projectFactory
				.createProject(dir, DependencyFacet.class, MetadataFacet.class,
						JavaSourceFacet.class, ResourceFacet.class);

		setBasicProjectData(globalProjectName, projectFinalName, project);

		PackagingFacet packaging = project.getFacet(PackagingFacet.class);
		packaging.setPackagingType(PackagingType.JAR);

		// Set name of the project
		packaging.setFinalName(projectFinalName);

		// Set the pom parent
		setPomParent(globalProjectName, project);

		return project;

	}

	/**
	 * 
	 * Construction of an AJF LIB type project
	 * 
	 * @param globalProjectName
	 * @param projectFinalName
	 * @param javaPackage
	 * @param projectFactory
	 * @param projectType
	 * @param dir
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Project generateProjectWS(String globalProjectName,
			String projectFinalName, String javaPackage,
			ProjectFactory projectFactory, String projectType,
			DirectoryResource dir) {

		Project project;
		project = projectFactory
				.createProject(dir, DependencyFacet.class, MetadataFacet.class,
						JavaSourceFacet.class, ResourceFacet.class);

		setBasicProjectData(globalProjectName, projectFinalName, project);

		PackagingFacet packaging = project.getFacet(PackagingFacet.class);
		packaging.setPackagingType(PackagingType.WAR);

		// Set name of the project
		packaging.setFinalName(projectFinalName);

		// Set the pom parent
		setPomParent(globalProjectName, project);

		/*
		 * Create an src/main/webapp package
		 */
		ProjectUtils.generateWebAppDirectory(project);

		return project;

	}

	/**
	 * Add a dependency in the pom.xml of the input project to the project (of
	 * the same global project) corresponding to the input dependencyProjectType
	 * 
	 * For example : Global project ajf-project
	 * 
	 * if input Project=ajf-project-core and dependencyProjectType="lib" then a
	 * dependency on ajf-project-lib will be added to the ajf-project-core's
	 * pom.xml
	 * 
	 * @param globalProjectName
	 * @param project
	 *            object of the current project beeing generated
	 * @param dependencyProjectType
	 */
	private void addDependency(String globalProjectName, Project project,
			String dependencyProjectType) {

		// Get the MavenFacet in order to grab the pom
		MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mavenCoreFacet.getPOM();

		// Create the dependecy
		Dependency dependency = new Dependency();
		dependency.setGroupId(groupId);
		dependency.setVersion("1.0.0-SNAPSHOT");
		dependency.setArtifactId(globalProjectName + "-"
				+ dependencyProjectType);

		// Add the dependency to the pom
		pom.addDependency(dependency);
		mavenCoreFacet.setPOM(pom);

	}

	/**
	 * Insert in the pom.xml of the input project, the parent corresponding to
	 * the globalProjectname-parent according to the ajf convention
	 * 
	 * @param globalProjectName
	 * @param project
	 */
	private void setPomParent(String globalProjectName, Project project) {

		// Get the Pom
		MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mavenCoreFacet.getPOM();

		// Create the parent
		Parent parent = new Parent();
		parent.setGroupId(groupId);
		parent.setArtifactId(globalProjectName + "-"
				+ ProjectUtils.PROJECT_TYPE_PARENT);

		parent.setVersion("1.0.0-SNAPSHOT");
		parent.setRelativePath("../" + globalProjectName + "-"
				+ ProjectUtils.PROJECT_TYPE_PARENT + "/pom.xml");

		// Set the parent to the pom of the project
		pom.setParent(parent);
		mavenCoreFacet.setPOM(pom);
	}

	/**
	 * Set basic project meta data : project name and repository dependency
	 * 
	 * @param finalProjectName
	 * @param javaPackage
	 * @param project
	 * @return
	 */
	private DependencyFacet setBasicProjectData(String globalProjectName,
			String finalProjectName, Project project) {

		// Set metadata
		MetadataFacet meta = project.getFacet(MetadataFacet.class);

		// group-ID
		groupId = "am.projects." + globalProjectName;
		meta.setTopLevelPackage(groupId);

		// Artifact ID
		meta.setProjectName(finalProjectName);

		// Dependencies
		DependencyFacet deps = project.getFacet(DependencyFacet.class);
		// deps.addRepository(KnownRepository.JBOSS_NEXUS);

		return deps;
	}

	/**
	 * Initialization of the project directory
	 * 
	 * @param projectFolder
	 * @return
	 * @throws Exception
	 */
	private DirectoryResource initializeProjectDirectory(
			Resource<?> projectFolder) throws Exception {
		// Check Project Directory
		DirectoryResource dir = null;
		if (projectFolder instanceof FileResource<?>) {

			if (!projectFolder.exists()) {

				// Creation of folder
				((FileResource<?>) projectFolder).mkdirs();
				dir = projectFolder.reify(DirectoryResource.class);

			} else if (projectFolder instanceof DirectoryResource) {
				dir = (DirectoryResource) projectFolder;

			} else {
				String errorMessage = "The root directory of the project beeing generated is incorrect";
				System.out.println(errorMessage);
				throw new Exception(errorMessage);
			}
		}

		// Create folder
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}
}