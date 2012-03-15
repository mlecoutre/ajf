package am.ajf.core.util;

import static am.ajf.forge.lib.ForgeConstants.*;
import static am.ajf.forge.lib.ForgeConstants.AJF_INJECTION;
import static am.ajf.forge.lib.ForgeConstants.AJF_PERSISTENCE;
import static am.ajf.forge.lib.ForgeConstants.AJF_REMOTING;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.junit.Test;

import am.ajf.forge.util.ProjectUtils;

public class ProjectUtilsTest {

	@Test
	public void getPomFromFileTest() throws Exception {

		Model pom = ProjectUtils.getPomFromFile("pom-ui.xml");

		Plugin myPlugin = null;
		for (Plugin plugin : pom.getBuild().getPlugins()) {

			if ("maven-war-plugin".equals(plugin.getArtifactId())) {
				myPlugin = plugin;
				break;
			}
		}

		assertNotNull("plugin war should not be null", myPlugin);

		System.out.println("PLUGIN CONF : "
				+ myPlugin.getConfiguration().toString());

	}

	@Test
	public void setAjfDependenciesFromFileTest() throws Exception {

		List<String> ajfDependencies = new ArrayList<String>();

		ajfDependencies.add(AJF_CORE);
		ajfDependencies.add(AJF_INJECTION);
		ajfDependencies.add(AJF_PERSISTENCE);
		ajfDependencies.add(AJF_REMOTING);

		Model pom = new Model();

		ProjectUtils.addAjfDependenciesToPom(ajfDependencies,
				AJF_DEPS_MODEL_FILE, pom);

		for (Dependency dep : pom.getDependencies()) {

			System.out.println("Found dependency : " + dep.getArtifactId());

		}

		assertTrue("should find 4 dependency",
				pom.getDependencies().size() == 4);

	}
}
