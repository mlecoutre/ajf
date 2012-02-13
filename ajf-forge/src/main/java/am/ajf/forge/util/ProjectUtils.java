package am.ajf.forge.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;

public class ProjectUtils {

	public static final String PROJECT_TYPE_PARENT = "parent";
	public static final String PROJECT_TYPE_EAR = "ear";
	public static final String PROJECT_TYPE_CORE = "core";
	public static final String PROJECT_TYPE_WS = "ws";
	public static final String PROJECT_TYPE_UI = "ui";
	public static final String PROJECT_TYPE_LIB = "lib";
	public static final String PROJECT_TYPE_CONFIG = "config";

	// Path name of the maven pref example file (root in main/resource
	// directory)
	private static final String MAVEC_PREF_EXAMPLE_FILE = "/MavenPrefs";

	/**
	 * Generate the .project file in the project directory in order for the
	 * project to be importable in Eclipse. This file is an XML file that
	 * contains :
	 * 
	 * <?xml version="1.0"?><projectDescription><name>ProjectName</name>
	 * <buildSpec> <buildCommand>
	 * <name>org.maven.ide.eclipse.maven2Builder</name> <arguments> </arguments>
	 * </buildCommand> </buildSpec><natures>
	 * <nature>org.maven.ide.eclipse.maven2Nature</nature> </natures>
	 * </projectDescription>
	 * 
	 * @param projectName
	 * @param projectRootDirectory
	 * @return File the .project file that has been generated my the method
	 * @throws FactoryConfigurationError
	 * @throws Exception
	 */
	public static File generateEclipseProjectFile(String projectName,
			String projectRootDirectory) throws FactoryConfigurationError,
			Exception {

		File myProjectFile = new File(projectRootDirectory + "/.project");
		// Create the .project file for eclipse
		try {

			XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

			myProjectFile.createNewFile();
			FileOutputStream stream = new FileOutputStream(myProjectFile);

			// XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();
			// XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

			/*
			 * Create a new XMLEventWriter object using the XMLOuputFactory
			 * Object The parameter denotes that the output will be done in XML.
			 */
			XMLEventWriter writer = outputFactory.createXMLEventWriter(stream);

			/*
			 * Creates a new instance of a StartDocument event First Parameters:
			 * Document Encoding Second Parameter: version
			 */
			StartDocument startDocument = xmlEventFactory.createStartDocument(
					"UTF-8", "1.0");
			/* Add the start document to the writer */
			writer.add(startDocument);

			/*
			 * Complete the document with the content
			 */

			// START element: <projectdescription>
			StartElement projectDescriptionStartElement = xmlEventFactory
					.createStartElement("", "", "projectDescription");
			writer.add(projectDescriptionStartElement);

			// START element : name of the project
			StartElement projectNameStartElement = xmlEventFactory
					.createStartElement("", "", "name");
			writer.add(projectNameStartElement);

			// Characters : name of the project
			Characters projectNameChar = xmlEventFactory
					.createCharacters(projectName);
			writer.add(projectNameChar);

			// END Element : </name> for the project name
			EndElement projectnameEndElement = xmlEventFactory
					.createEndElement("", "", "name");
			writer.add(projectnameEndElement);

			/*
			 * Build Specs
			 */

			// START element : <buildSpec>
			writer.add(xmlEventFactory.createStartElement("", "", "buildSpec"));

			// Add the build Commands
			addBuildCommand(xmlEventFactory, writer,
					"org.eclipse.wst.common.project.facet.core.builder");
			addBuildCommand(xmlEventFactory, writer,
					"org.eclipse.jdt.core.javabuilder");
			addBuildCommand(xmlEventFactory, writer,
					"org.maven.ide.eclipse.maven2Builder");

			// END element : </buildSpec>
			writer.add(xmlEventFactory.createEndElement("", "", "buildSpec"));

			/*
			 * NATURES
			 */

			// START element : <natures>
			writer.add(xmlEventFactory.createStartElement("", "", "natures"));

			// Add natures values
			addNature(xmlEventFactory, writer,
					"org.maven.ide.eclipse.maven2Nature");
			addNature(xmlEventFactory, writer,
					"org.eclipse.jdt.core.javanature");
			addNature(xmlEventFactory, writer,
					"org.eclipse.wst.common.project.facet.core.nature");

			// END element : natures
			writer.add(xmlEventFactory.createEndElement("", "", "natures"));

			// End element : </projectDescription>
			EndElement projectDescriptionEndElement = xmlEventFactory
					.createEndElement("", "", "projectDescription");
			writer.add(projectDescriptionEndElement);

			// Flush = Write ^physically in the file
			writer.flush();

			// Close resources
			writer.close();
			stream.close();

			return myProjectFile;

		} catch (FileNotFoundException e) {
			System.out
					.println("** generateEclipseProjectFile - FileNotFoundException occured"
							+ e.toString());
			throw e;
		} catch (IOException e) {
			System.out
					.println("** generateEclipseProjectFile - IOException occured"
							+ e.toString());
			throw e;
		} catch (XMLStreamException e) {
			System.out
					.println("** generateEclipseProjectFile - XMLStreamException occured"
							+ e.toString());
			throw e;
		} catch (Exception e) {

			System.out
					.println("** generateEclipseProjectFile - Exception occured"
							+ e.toString());
			throw e;
		}
	}

	private static void addNature(XMLEventFactory xmlEventFactory,
			XMLEventWriter writer, String NatureValue)
			throws XMLStreamException {
		// START element : nature
		writer.add(xmlEventFactory.createStartElement("", "", "nature"));

		// Build command name value
		writer.add(xmlEventFactory.createCharacters(NatureValue));

		// END element : nature
		writer.add(xmlEventFactory.createEndElement("", "", "nature"));
	}

	private static void addBuildCommand(XMLEventFactory xmlEventFactory,
			XMLEventWriter writer, String buildCommandName)
			throws XMLStreamException {
		// START element : <buildCommand>
		writer.add(xmlEventFactory.createStartElement("", "", "buildCommand"));

		// START element : <name> (for build command)
		writer.add(xmlEventFactory.createStartElement("", "", "name"));

		// Build command name value
		writer.add(xmlEventFactory.createCharacters(buildCommandName));

		// END element : </name>
		writer.add(xmlEventFactory.createEndElement("", "", "name"));

		// END element : </buildCommand>
		writer.add(xmlEventFactory.createEndElement("", "", "buildCommand"));
	}

	/**
	 * Generate the .settings directory in the root dorectory of the project.
	 * This .settings folder contains a 'org.maven.ide.eclipse.prefs' file
	 * containing the maven preferences for Eclipse (such as the maven profile)
	 * 
	 * @param projectRootDirectory
	 * @throws Exception
	 */
	public static File generateEclipseMavenPrefFile(String projectRootDirectory)
			throws Exception {

		// Create .Settings directory (at the root directory of the project)
		File mavenPrefRepo = new File(projectRootDirectory + "/.settings");
		mavenPrefRepo.mkdirs();

		// Create the file
		File mavenPrefFile = new File(mavenPrefRepo.getAbsolutePath().concat(
				"/org.maven.ide.eclipse.prefs"));

		try {

			// Remove the file if it already exists
			if (mavenPrefFile.exists()) {
				mavenPrefFile.delete();
			}

			// Create the file
			mavenPrefFile.createNewFile();

			// Open an outputStream on the file
			FileOutputStream mavenFileStream = new FileOutputStream(
					mavenPrefFile);

			// Open an input stream on the example file
			InputStream myStream = ProjectUtils.class
					.getResourceAsStream(MAVEC_PREF_EXAMPLE_FILE);

			// In case the file is not found
			if (myStream == null) {

				System.out.println("Maven prefs file does not exist");
				throw new Exception(
						"Error :Maven prefs file does not existMaven prefs file does not exist");
			}

			// Read input file and append output file
			int c;
			while ((c = myStream.read()) != -1) {
				mavenFileStream.write(c);
			}

			// Close Streams
			myStream.close();
			mavenFileStream.close();

			return mavenPrefFile;

		} catch (IOException e) {
			System.out.println("IOException occured");
			throw e;
		}
	}

	public static File generateClassPathFile(String projectRootDirectory)
			throws Exception {

		File myProjectFile = new File(projectRootDirectory + "/.classpath");
		// Create the .project file for eclipse
		try {

			XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

			myProjectFile.createNewFile();
			FileOutputStream stream = new FileOutputStream(myProjectFile);

			/*
			 * Create a new XMLEventWriter object using the XMLOuputFactory
			 * Object The parameter denotes that the output will be done in XML.
			 */
			XMLEventWriter writer = outputFactory.createXMLEventWriter(stream);

			/*
			 * Creates a new instance of a StartDocument event First Parameters:
			 * Document Encoding Second Parameter: version
			 */
			StartDocument startDocument = xmlEventFactory.createStartDocument(
					"UTF-8", "1.0");
			/* Add the start document to the writer */
			writer.add(startDocument);

			writer.add(xmlEventFactory.createStartElement("", "", "classpath"));

			// TODO ClassPath is different depending on the project Type.
			// Example: config does not have src/main/java

			// Classpath entry for src main java
			addClassPathEntry(xmlEventFactory, writer, "src", "src/main/java");
			addClassPathEntry(xmlEventFactory, writer, "src", "src/test/java");
			addClassPathEntry(xmlEventFactory, writer, "src",
					"src/main/resources");
			addClassPathEntry(xmlEventFactory, writer, "src",
					"src/test/resources");
			addClassPathEntry(xmlEventFactory, writer, "con",
					"org.eclipse.jdt.launching.JRE_CONTAINER");
			addClassPathEntry(xmlEventFactory, writer, "output", "bin");

			writer.add(xmlEventFactory.createEndElement("", "", "classpath"));

			// Flush = Write ^physically in the file
			writer.flush();

			// Close resources
			writer.close();
			stream.close();

			return myProjectFile;

		} catch (Exception e) {

			throw e;

		}
	}

	/**
	 * Set a classpath entry in the .classpath file of the project
	 * 
	 * <classpathentry kind="kindValue" path="pathValue"/>
	 * 
	 * @param writer
	 * @param kindValue
	 * @param pathvalue
	 * @throws XMLStreamException
	 */
	private static void addClassPathEntry(XMLEventFactory xmlEventFactory,
			XMLEventWriter writer, String kindValue, String pathvalue)
			throws XMLStreamException {

		writer.add(xmlEventFactory.createStartElement("", "", "classpathentry"));
		writer.add(xmlEventFactory.createAttribute("kind", kindValue));
		writer.add(xmlEventFactory.createAttribute("path", pathvalue));
		writer.add(xmlEventFactory.createEndElement("", "", "classpathentry"));
	}

}
