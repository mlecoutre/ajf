package am.ajf.forge.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;

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
	 * Uses a model reousrce pom.xml file and sets the dependencies, properties,
	 * profiles and the whole build sections to the generated project's pom.xml
	 * file
	 * 
	 * @param project
	 * @param resourcePomFile
	 * @throws Exception
	 */
	public static void setUIPomFromFile(Project project,
			String resourcePomFile, boolean isCompacted) throws Exception {

		Model resourcePom = ProjectUtils.getPomFromFile(resourcePomFile);

		MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
		Model generatedPom = mavenCoreFacet.getPOM();

		// Set Properties only in compacted mode
		if (isCompacted)
			generatedPom.setProperties(resourcePom.getProperties());

		// Set dependencies
		generatedPom.setDependencies(resourcePom.getDependencies());

		// set Profiles
		generatedPom.setProfiles(resourcePom.getProfiles());

		// set build Plugins
		// generatedPom.getBuild().setPlugins(resourcePom.getBuild().getPlugins());
		generatedPom.setBuild(resourcePom.getBuild());

		mavenCoreFacet.setPOM(generatedPom);
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

	/**
	 * Generate a managed bean class to the current project. It uses as package
	 * name the javaPackage input params, suffixed of web.controllers :
	 * 
	 * 'javaPackage'.web.controllers.
	 * 
	 * @param javaPackage
	 * @param project
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	public static void generateManagedBeanClass(String javaPackage,
			Project project) throws Exception {

		try {
			System.out.println("** DEBUG : START generating java class for UI");

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

			javaclass.addImport("javax.faces.bean.ManagedBean");

			// Save the java class in the project
			javaSourcefacet.saveJavaSource(javaclass);

			System.out.println("**DEBUG : END Java class MBean generated.");

		} catch (Exception e) {
			String message = "Error when generating example java Managed bean file";
			System.err.println("** ERROR : ".concat(message));
			throw new Exception(message, e);
		}

	}

}
