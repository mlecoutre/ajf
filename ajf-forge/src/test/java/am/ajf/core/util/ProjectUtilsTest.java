package am.ajf.core.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.junit.Test;

import am.ajf.forge.util.UIProjectUtils;

public class ProjectUtilsTest {

	@Test
	public void manageTomCatPLuginDependenciesTest()
			throws FactoryConfigurationError, XMLStreamException {

		Model pom = new Model();
		pom.setArtifactId("pomTest");

		Plugin plugin = new Plugin();
		plugin.setGroupId("org.apache.tomcat.maven");
		plugin.setArtifactId("tomcat7-maven-plugin");
		plugin.setVersion("2.0-beta-1");

		final String TOMCAT_PLUGIN_FILE = "tomcatPlugin.xml";
		List<Dependency> dependcies = UIProjectUtils
				.manageDependenciesFromFile(TOMCAT_PLUGIN_FILE);

		int nbDependcies = dependcies.size();

		assertNotNull("method should not return null", nbDependcies);

		System.out.println("found " + nbDependcies
				+ " dependecies in tomcat plugin");

		logDependencyList(dependcies);

		assertTrue(
				"More than 0 dependencies should be returned by the tomcat plugin dependencies retrieval",
				nbDependcies > 0);

	}

	@Test
	public void manageUIProjectDependencies() throws FactoryConfigurationError,
			XMLStreamException {

		Model pom = new Model();
		pom.setArtifactId("pomTest2");

		final String UI_DEPENDENCIES_FILE = "UIprojectDependencies.xml";
		List<Dependency> dependcies = UIProjectUtils
				.manageDependenciesFromFile(UI_DEPENDENCIES_FILE);

		int nbDependcies = dependcies.size();

		assertNotNull(" method should not return null", nbDependcies);

		System.out.println("found " + nbDependcies + " dependecies ");

		logDependencyList(dependcies);

		assertTrue("More than 0 dependencies should be returned",
				nbDependcies > 0);

	}

	private void logDependencyList(List<Dependency> deps) {

		for (Dependency dep : deps) {

			System.out.println("<dependency>");
			System.out.println("<groupIdId>" + dep.getGroupId() + "</groupId>");
			System.out.println("<artifactId>" + dep.getArtifactId()
					+ "</artifactId>");
			System.out.println("<version>" + dep.getVersion() + "</version>");

			if (!dep.getExclusions().isEmpty()) {

				System.out.println("<exclusions>");
				for (Exclusion excl : dep.getExclusions()) {
					System.out.println("<exclusion>");

					System.out.println("<groupIdId>" + excl.getGroupId()
							+ "</groupId>");
					System.out.println("<artifactId>" + excl.getArtifactId()
							+ "</artifactId>");

					System.out.println("</exclusion>");
				}

				System.out.println("</exclusions>");

			}

		}

	}
}
