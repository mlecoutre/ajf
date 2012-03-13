package am.ajf.core.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.junit.Test;

import am.ajf.forge.util.UIProjectUtils;

public class ProjectUtilsTest {

	@Test
	public void manageDependenciesTest() throws FactoryConfigurationError,
			XMLStreamException {

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

		assertNotNull(
				"UIProjectUtils.manageTomcatDependeciesFromFile sholuld not return null",
				nbDependcies);

		System.out.println("found " + nbDependcies
				+ " dependecies in tomcat plugin");

		assertTrue(
				"More than 0 dependencies should be returned by the tomcat plugin dependencies retrieval",
				nbDependcies > 0);

	}

}
