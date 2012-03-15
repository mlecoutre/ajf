package am.ajf.forge.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import org.apache.maven.model.Activation;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Profile;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import static am.ajf.forge.lib.ForgeConstants.*;

public class UIProjectUtils {

	/**
	 * Unzip zip file corresponding to input resourceZipFileName to the
	 * destination folder set as input parameter
	 * 
	 * @param resourceZipFileName
	 *            : Path to the zip resource to be unzipped
	 * @param destinationFolder
	 *            : where to unzip
	 * @throws IOException
	 */
	public static boolean unzipFile(String resourceZipFileName,
			File destinationFolder) throws IOException {

		InputStream resourceZipFileStream = UIProjectUtils.class
				.getClassLoader().getResourceAsStream(resourceZipFileName);

		byte[] buf = new byte[1024];

		ZipEntry zipentry;
		ZipInputStream zipInputStream = new ZipInputStream(
				resourceZipFileStream);

		zipentry = zipInputStream.getNextEntry();

		while (zipentry != null) {
			// for each entry to be extracted
			String entryName = zipentry.getName();
			// System.out.println("entryname " + entryName);
			int n;
			FileOutputStream fileoutputstream;
			File newFile = new File(entryName);
			String directory = newFile.getParent();

			if (directory == null) {
				if (newFile.isDirectory())
					break;
			}

			File myFile = new File(destinationFolder + "/" + entryName);

			if (!myFile.isDirectory() && entryName.contains(".")) {

				if (!myFile.exists()) {
					myFile.getParentFile().mkdirs();
					myFile.createNewFile();
				}
				fileoutputstream = new FileOutputStream(myFile);

				while ((n = zipInputStream.read(buf, 0, 1024)) > -1)
					fileoutputstream.write(buf, 0, n);

				fileoutputstream.close();
				fileoutputstream = null;
			}
			zipInputStream.closeEntry();
			zipentry = zipInputStream.getNextEntry();
			myFile = null;
			newFile = null;

		}// while

		zipInputStream.close();

		zipInputStream = null;
		zipentry = null;

		return true;

	}

	/**
	 * Set to the current project's pom.xml, the tomcat plugin with all it's
	 * dependencies defined in the resources tomcatPlugin.xml
	 * 
	 * @param project
	 * @throws IOException
	 */
	public static void setTomCatPlugin(Project project) throws IOException {

		try {
			MavenCoreFacet mavenCoreFacet = project
					.getFacet(MavenCoreFacet.class);
			Model pom = mavenCoreFacet.getPOM();

			Plugin plugin = new Plugin();
			plugin.setGroupId(TOMCAT_PLUGIN_GROUPID);
			plugin.setArtifactId(TOMCAT_PLUGIN_ARTIFACTID);
			plugin.setVersion(TOMCAT_PLUGIN_VERSION);

			/*
			 * Plugin configuration
			 */
			Xpp3Dom config = new Xpp3Dom("configuration");

			// Port
			Xpp3Dom configPort = new Xpp3Dom("port");
			configPort.setValue("8080");
			config.addChild(configPort);
			configPort = null;

			// contextFile
			Xpp3Dom configContextFile = new Xpp3Dom(("contextFile"));
			configContextFile.setValue(TOMCAT_DEPENDENCIES_FILE);
			config.addChild(configContextFile);
			configContextFile = null;

			// warFile
			Xpp3Dom configWarFile = new Xpp3Dom(("warFile"));
			configWarFile.setValue("target/${project.artifactId}.war");
			config.addChild(configWarFile);
			configWarFile = null;

			// Path
			Xpp3Dom configPath = new Xpp3Dom(("path"));
			configPath.setValue("/${project.artifactId}");
			config.addChild(configPath);
			configPath = null;

			/*
			 * Plugin dependencies
			 */
			List<Dependency> dependencyList = manageDependenciesFromFile(TOMCAT_DEPENDENCIES_FILE);
			for (Dependency dep : dependencyList) {
				plugin.addDependency(dep);
			}

			// Add plugin to the pom.xml
			List<Plugin> plugins = new ArrayList<Plugin>();
			plugins.add(plugin);
			pom.getBuild().setPlugins(plugins);
			mavenCoreFacet.setPOM(pom);

		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Set TOMCAT7 and WAS 7 profiles to the input project's pom.xml. XML
	 * resources files are used to extract each profile's dependencies
	 * 
	 * @param project
	 * @throws XMLStreamException
	 */
	public static void setProfiles(Project project) throws XMLStreamException {

		MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mavenCoreFacet.getPOM();

		List<Profile> profiles = new ArrayList<Profile>();

		/*
		 * TOMCAT 7 PROFILE
		 */
		Profile tomCatProfile = new Profile();
		tomCatProfile.setId("TOMCAT7");
		Activation tomcatProfileActivation = new Activation();
		tomcatProfileActivation.setActiveByDefault(true);
		tomCatProfile.setActivation(tomcatProfileActivation);

		List<Dependency> tomcatProfileDeps = manageDependenciesFromFile(TOMCAT_PLUGIN_PROFILE_DEPENDENCIES_FILE);

		for (Dependency dep : tomcatProfileDeps) {
			tomCatProfile.addDependency(dep);
		}

		profiles.add(tomCatProfile);
		tomCatProfile = null;

		/*
		 * WAS 7 Profile
		 */
		Profile wasProfile = new Profile();
		wasProfile.setId("WAS7");
		Activation activation = new Activation();
		activation.setActiveByDefault(false);
		wasProfile.setActivation(activation);

		List<Dependency> wasProfileDeps = manageDependenciesFromFile(WAS_PROFILE_DEPENDENCIES_FILE);

		for (Dependency dep : wasProfileDeps) {
			wasProfile.addDependency(dep);
		}
		profiles.add(wasProfile);
		wasProfile = null;

		/*
		 * Add Profiles to project's pom.xml
		 */
		pom.setProfiles(profiles);
		mavenCoreFacet.setPOM(pom);

	}

	/**
	 * Browse resources xml file which describe all the needed dependencies for
	 * a UI project. Extracts and set dependencies to current project's pom.xml
	 * 
	 * @param project
	 * @throws FactoryConfigurationError
	 * @throws XMLStreamException
	 */
	public static void setUIDependencies(Project project)
			throws FactoryConfigurationError, XMLStreamException {

		MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mavenCoreFacet.getPOM();

		// Add properties to the pom
		Properties properties = new Properties();
		properties.setProperty("ajfversion", "2.1.0-SNAPSHOT");
		properties.setProperty("openwebbeansVersion", "1.1.3");
		pom.setProperties(properties);

		// Add dependency list from resource XML file
		List<Dependency> dependencies = UIProjectUtils
				.manageDependenciesFromFile("UIprojectDependencies.xml");

		for (Dependency dep : dependencies) {
			pom.addDependency(dep);
		}

		mavenCoreFacet.setPOM(pom);
	}

	/**
	 * Use the tomcat plug in XML file in order to add all dependcy to the
	 * current project's pom.xml. We use a list of dependecyVO object in order
	 * to be able to test this method (without creating a pom.xml)
	 * 
	 * @param xmlFileContainingDependencies
	 * @return number of dependency found
	 * @throws FactoryConfigurationError
	 * @throws XMLStreamException
	 */
	public static List<Dependency> manageDependenciesFromFile(
			String xmlFileContainingDependencies)
			throws FactoryConfigurationError, XMLStreamException {

		// STAX init
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

		InputStream is = ProjectUtils.class.getClassLoader()
				.getResourceAsStream(xmlFileContainingDependencies);

		XMLStreamReader xmlsr = xmlInputFactory.createXMLStreamReader(is);

		XMLEventReader xmleventReader = xmlInputFactory
				.createXMLEventReader(xmlsr);

		List<Dependency> dependencyList = new ArrayList<Dependency>();

		// init the output List of dependency
		// List<DependencyVO> dependencyList = new ArrayList<DependencyVO>();

		while (xmleventReader.hasNext()) {

			XMLEvent event = (XMLEvent) xmleventReader.next();

			// We wait for the start of 'dependencies' section
			if (isStartElementEqualsTo(event, "dependencies")) {

				// While it's not the end of dependencies : </dependecies>
				while (!isEndDependencies(event)) {

					event = (XMLEvent) xmleventReader.next();

					// If we find event = <dependency>
					if (isStartElementEqualsTo(event, "dependency")) {

						// Create a dependencyVO object (to stored dependencies)
						Dependency myDependency = new Dependency();
						// printEventValue(event);

						while (!isEndElementEqualsTo(event, "dependency")) {
							event = (XMLEvent) xmleventReader.next();
							manageDependencies(xmleventReader, myDependency,
									event);
						}
						// Add the current dependency to plugin
						dependencyList.add(myDependency);
					}
				}
			}
		}

		return dependencyList;
	}

	/**
	 * Return event name whatever it's type
	 * 
	 * @param event
	 */
	private static String getEventValue(XMLEvent event) {
		if (event.isStartElement())
			return event.asStartElement().getName().toString();
		if (event.isEndElement())
			return event.asEndElement().getName().toString();
		if (event.isCharacters())
			return event.asCharacters().getData();
		return null;
	}

	/**
	 * Loop on dependencies on the input XML containing a list of dependencies
	 * and create output dependency object
	 * 
	 * @param xmleventReader
	 * @param myDependency
	 */
	private static void manageDependencies(XMLEventReader xmleventReader,
			Dependency myDependency, XMLEvent event) {

		if (isStartElementEqualsTo(event, "groupId")) {

			/*
			 * Dependecy - Group Id
			 */
			event = (XMLEvent) xmleventReader.next();
			String groupId = event.asCharacters().getData();
			myDependency.setGroupId(groupId);

		} else if (isStartElementEqualsTo(event, "artifactId")) {

			/*
			 * Dependecy - artifactId
			 */
			event = (XMLEvent) xmleventReader.next();
			String artifactId = event.asCharacters().getData();
			myDependency.setArtifactId(artifactId);

		} else if (isStartElementEqualsTo(event, "version")) {

			/*
			 * Dependecy - version
			 */
			event = (XMLEvent) xmleventReader.next();
			String version = event.asCharacters().getData();
			myDependency.setVersion(version);

		} else if (isStartElementEqualsTo(event, "scope")) {

			event = (XMLEvent) xmleventReader.next();
			String scope = event.asCharacters().getData();
			myDependency.setScope(scope);

		} else if (isStartElementEqualsTo(event, "exclusions")) {

			manageDependecyExclusions(xmleventReader, event, myDependency);
		}
	}

	/**
	 * 
	 * Manage exclusions dealing with the current dependency beeing extracted
	 * from XML source file and pass it to the output dependency object
	 * 
	 * @param xmleventReader
	 * @param event
	 */
	private static void manageDependecyExclusions(
			XMLEventReader xmleventReader, XMLEvent event,
			Dependency myDependency) {

		// System.out.println("EXCLUSIONS START");
		// Manage exclusion
		while (!isEndElementEqualsTo(event, "exclusions")) {

			event = (XMLEvent) xmleventReader.next();

			if (isStartElementEqualsTo(event, "exclusion")) {

				Exclusion exclusion = new Exclusion();
				// System.out.println("EXCLUSION START");
				while (!isEndElementEqualsTo(event, "exclusion")) {

					event = (XMLEvent) xmleventReader.next();

					if (isStartElementEqualsTo(event, "groupId")) {

						event = (XMLEvent) xmleventReader.next();
						String groupId = event.asCharacters().getData();
						exclusion.setGroupId(groupId);

					} else if (isStartElementEqualsTo(event, "artifactId")) {

						event = (XMLEvent) xmleventReader.next();
						String artifactId = event.asCharacters().getData();
						exclusion.setArtifactId(artifactId);
					}
				}
				// Add exclusion to dependeny
				myDependency.addExclusion(exclusion);
			}
		}
	}

	/**
	 * is the input event sent on the 'dependencies' end element.
	 * 
	 * @param event
	 * @return boolean: true if the event is on the dependency end element
	 */
	private static boolean isEndDependencies(XMLEvent event) {

		boolean bool = event.isEndElement()
				&& "dependencies".equals(event.asEndElement().getName()
						.toString());
		return bool;

	}

	/**
	 * Is the input event is a START XML element ? And is it equal to the input
	 * value ?
	 * 
	 * @param event
	 * @param value
	 * @return boolean : true if both condition are verified
	 */
	private static boolean isStartElementEqualsTo(XMLEvent event, String value) {

		return event.isStartElement()
				&& value.equals(event.asStartElement().getName().toString());
	}

	/**
	 * Is the input event is an END XML element ? And is it equal to the input
	 * value ?
	 * 
	 * @param event
	 * @param value
	 * @return
	 */
	private static boolean isEndElementEqualsTo(XMLEvent event, String value) {

		if (event.isEndElement()) {

			String endElemenValue = event.asEndElement().getName().toString();
			return value.equals(endElemenValue);

		} else {

			return false;
		}

	}

}
