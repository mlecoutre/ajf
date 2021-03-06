package am.ajf.core.util;

import static am.ajf.forge.lib.ForgeConstants.MODEL_POM_UI;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;

import org.apache.commons.lang.WordUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.junit.Test;

import am.ajf.forge.helpers.ProjectHelper;

public class ProjectUtilsTest {

	private ProjectHelper projectUtils = new ProjectHelper();

	@Test
	public void testGetPomFromFile() throws Exception {

		String profileToFindInPom = "TOMCAT7";

		Model pom = projectUtils.getPomFromFile(MODEL_POM_UI);

		Profile myProfile = null;
		for (Profile profile : pom.getProfiles()) {

			if (profileToFindInPom.equals(profile.getId())) {
				myProfile = profile;
				break;
			}
		}

		assertNotNull(
				"PROFILE "
						+ profileToFindInPom
						+ " should be found in this pom.xml file - Re adjust test to input data",
				myProfile);

	}

	@Test(expected = Exception.class)
	public void testGetPomFromFileFail() throws Exception {

		projectUtils.getPomFromFile("non-existing-file");
	}

	@Test
	public void testGenerateJavaClass() {

		JavaClass javaclass = JavaParser.create(JavaClass.class)
				.setPackage("test.voila.voila").setName("Voila").getOrigin();

		javaclass.addField().setPrivate().setFinal(true).setStatic(true)
				.setName("serialVersionUID").setLiteralInitializer("1L");

		javaclass.addInterface(Serializable.class);

		System.out.println(javaclass.toString());

	}
	// @Test
	// public void setAjfDependenciesFromFileTest() throws Exception {
	//
	// List<String> ajfDependencies = new ArrayList<String>();
	//
	// ajfDependencies.add(AJF_CORE);
	// ajfDependencies.add(AJF_INJECTION);
	// ajfDependencies.add(AJF_PERSISTENCE);
	// ajfDependencies.add(AJF_REMOTING);
	//
	// Model pom = new Model();
	//
	// ProjectUtils.addAjfDependenciesToPom(ajfDependencies,
	// AJF_DEPS_MODEL_FILE, pom);
	//
	// for (Dependency dep : pom.getDependencies()) {
	//
	// System.out.println("Found dependency : " + dep.getA/rtifactId());
	//
	// }
	//
	// assertTrue("should find 4 dependency",
	// pom.getDependencies().size() == 4);
	//
	// }

}
