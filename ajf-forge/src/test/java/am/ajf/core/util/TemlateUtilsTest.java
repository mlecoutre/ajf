package am.ajf.core.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.java.JavaResource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import am.ajf.forge.core.generators.templates.McrGenerationTemplate;
import am.ajf.forge.lib.EntityDTO;
import am.ajf.forge.util.JavaHelper;
import am.ajf.forge.util.TemplateUtils;

/**
 * 
 * Test a complete templating Test using FreeMarker. All the use of FreeMarker
 * API is done in templateUtils. This whole test case is commented as for the
 * moment FreeMarker make the forge plugin fail...
 * 
 * @author E019851
 */
public class TemlateUtilsTest {

	private static EntityDTO entityDto = new EntityDTO();
	private static File tmpDirectory;

	@BeforeClass
	public static void beforeClass() {

		tmpDirectory = new File(FileUtils.getTempDirectoryPath()
				+ "/ajf-forge-test");

		if (!tmpDirectory.exists())
			tmpDirectory.mkdirs();

	}

	@AfterClass
	public static void afterClass() {

		if (tmpDirectory.exists()) {
			try {
				FileUtils.cleanDirectory(tmpDirectory);
				FileUtils.forceDelete(tmpDirectory);
			} catch (IOException e) {
				System.err
						.println("Fail to delete temp Directory used for test : "
								+ tmpDirectory.getAbsolutePath());
				e.printStackTrace();
			}
		}

	}

	/**
	 * Test of the fail execution of Templating throw FreeMarker when the
	 * directory that should contain the templates fils does not exist
	 * 
	 * @throws Exception
	 */
	@Test(expected = FileNotFoundException.class)
	public void templateWrongTemplateDirTest() throws Exception {

		// Instanciate TemplateUtils for freeMarker stuff instanciation, with a
		// non existing directory (tht should contain the template files)
		new TemplateUtils("C:/NotExistingDirectory");

	}

	/**
	 * Test of the fail execution of Templating throw FreeMarker when a non
	 * existing template name is given
	 * 
	 * @throws Exception
	 */
	@Test(expected = FileNotFoundException.class)
	public void templateWrongTemplateNameTest() throws Exception {

		// Instanciate TemplateUtils and freeMarker with default template dir
		// (in resource)
		TemplateUtils templateUtils = new TemplateUtils();
		System.out.println("template utils instanciated.");

		// Find the a template in the project resources
		templateUtils.getTemplate("nonExistingTemplate.ftl");

	}

	@Test
	public void testBuildManagedBean() throws Exception {

		McrGenerationTemplate projectManagement = new McrGenerationTemplate();

		/*
		 * OutputFile
		 */
		File myFile = new File("C:/myGeneratedBean.java");
		if (myFile.exists())
			myFile.delete();

		myFile.createNewFile();

		List<String> uts = new ArrayList<String>();
		uts.add("addPerson");
		uts.add("deletePerson");
		uts.add("otherUt");

		projectManagement.buildManagedBean(myFile, "voila", "myFunctionName",
				"Person", entityDto.getEntityAttributeList(),
				"am.ajf.web.controllers.test", "am.ajf.voila.lib.business",
				"am.ajf.voila.lib.business.dto",
				entityDto.getEntityLibPackage(), uts);

	}

	@Test
	public void testBuildPolicy() throws Exception {

		McrGenerationTemplate projectManagement = new McrGenerationTemplate();

		/*
		 * OutputFile
		 */
		File myFile = new File("C:/myGeneratedBean.java");
		if (myFile.exists())
			myFile.delete();

		myFile.createNewFile();

		List<String> uts = new ArrayList<String>();
		uts.add("addPerson");
		uts.add("deletePerson");
		uts.add("otherUt");

		projectManagement.buildPolicy(myFile, "myFunctionName", uts,
				entityDto.getEntityLibPackage(),
				entityDto.getEntityLibPackage() + ".dto",
				"am.ajf.voila.core.test");

	}

	@Test
	public void testBuildBusinessDelegate() throws Exception {

		McrGenerationTemplate projectManagement = new McrGenerationTemplate();

		/*
		 * OutputFile
		 */
		File myFile = new File("C:/myGeneratedBean.java");
		if (myFile.exists())
			myFile.delete();

		myFile.createNewFile();

		List<String> uts = new ArrayList<String>();
		uts.add("addPerson");
		uts.add("deletePerson");
		uts.add("otherUt");

		projectManagement.buildBusinessDelegateInterface(myFile,
				entityDto.getEntityLibPackage(), "myFunctionName", uts,
				entityDto.getEntityLibPackage() + ".dto");

	}

	@Test
	public void testBuildManagedBeanMethod() throws Exception {
		McrGenerationTemplate projectManagement = new McrGenerationTemplate();

		File tmpFile = new File(tmpDirectory.getAbsolutePath() + "/voila.tmp");

		projectManagement.buildManagedBeanMethod(tmpFile, "myFunctionName",
				"utName");

		assertTrue("file shouldn't be empty", tmpFile.length() > 0);

		// tmpFile.setWritable(true);
		tmpFile.delete();

	}

	@Test
	public void testBuildXhtmlWithAddDelete() throws Exception {

		McrGenerationTemplate projectManagement = new McrGenerationTemplate();

		File myFile = new File("C:/myCrudXhtml.xhtml");

		if (myFile.exists())
			myFile.delete();

		myFile.createNewFile();

		List<String> uts = new ArrayList<String>();
		uts.add("addPerson");
		uts.add("deletePerson");
		uts.add("otherUt");

		projectManagement.buildXhtml(myFile, "voila", "myGeneratedBean",
				"Person", entityDto.getEntityAttributeList(),
				"am.ajf.web.controllers.test", entityDto.getEntityLibPackage(),
				uts);

	}

	@Test
	public void testBuildXhtmlWithAddOnly() throws Exception {

		McrGenerationTemplate projectManagement = new McrGenerationTemplate();

		File myFile = new File("C:/myCrudXhtml.xhtml");

		if (myFile.exists())
			myFile.delete();

		myFile.createNewFile();

		List<String> uts = new ArrayList<String>();
		uts.add("addPerson");
		uts.add("otherUt");

		projectManagement.buildXhtml(myFile, "voila", "myGeneratedBean",
				"Person", entityDto.getEntityAttributeList(),
				"am.ajf.web.controllers.test", entityDto.getEntityLibPackage(),
				uts);

	}

	@Test
	public void testBuildXhtmlWithDeleteOnly() throws Exception {

		McrGenerationTemplate projectManagement = new McrGenerationTemplate();

		File myFile = new File("C:/myCrudXhtml.xhtml");

		if (myFile.exists())
			myFile.delete();
		myFile.createNewFile();

		List<String> uts = new ArrayList<String>();
		uts.add("deletePerson");
		uts.add("otherUt");

		projectManagement.buildXhtml(myFile, "voila", "myGeneratedBean",
				"Person", entityDto.getEntityAttributeList(),
				"am.ajf.web.controllers.test", entityDto.getEntityLibPackage(),
				uts);

	}

	@Test
	public void testBuildXhtmlWithNoAddDelete() throws Exception {

		McrGenerationTemplate projectManagement = new McrGenerationTemplate();

		File myFile = new File("C:/myCrudXhtml.xhtml");

		if (myFile.exists())
			myFile.delete();

		myFile.createNewFile();

		List<String> uts = new ArrayList<String>();
		uts.add("otherUt");
		uts.add("otherUt2");
		uts.add("otherUt3");
		uts.add("otherUt4");
		uts.add("otherUt5");

		projectManagement.buildXhtml(myFile, "voila", "myGeneratedBean",
				"Person", entityDto.getEntityAttributeList(),
				"am.ajf.web.controllers.test", entityDto.getEntityLibPackage(),
				uts);

	}

	@SuppressWarnings("rawtypes")
	@BeforeClass
	public static void init() throws Exception {
		/*
		 * Retrieve AttributeList of resources Java model Class
		 */
		File javaClass = new File(TemlateUtilsTest.class.getClassLoader()
				.getResource("Person.java").toURI());
		ResourceFactory factory = new ResourceFactory();
		JavaSource javaSource = new JavaResource(factory, javaClass)
				.getJavaSource();

		JavaHelper javaUtils = new JavaHelper();
		entityDto.setEntityAttributeList(javaUtils
				.retrieveAttributeList(javaSource));

		entityDto.setEntityLibPackage("am.ajf.myProject.lib.model");

	}

}
