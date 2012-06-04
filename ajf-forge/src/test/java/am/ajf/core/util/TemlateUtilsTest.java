package am.ajf.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.java.JavaResource;
import org.junit.BeforeClass;
import org.junit.Test;

import am.ajf.forge.core.CrudGeneration;
import am.ajf.forge.lib.EntityDTO;
import am.ajf.forge.util.JavaUtils;
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

	static EntityDTO entityDto = new EntityDTO();

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

	// @SuppressWarnings("unchecked")
	// @Test
	// public void javaClassMBeanTemplate() throws Exception {
	//
	// TemplateUtils templateUtils = new TemplateUtils();
	// System.out.println("template utils instanciated.");
	//
	// // Find the a template in the project resources
	// Template myTemplate = templateUtils.getTemplate("classJava.ftl");
	// System.out.println("template".concat("classJava.ftl").concat(
	// " located."));
	//
	// // Generate an my data model data model
	// Map root = new HashMap();
	//
	// Map function = new HashMap();
	// function.put("MbeanName", "myGeneratedManagedBean");
	//
	// SimpleSequence uts = new SimpleSequence();
	//
	// Map ut1 = new HashMap();
	// ut1.put("methodName", "displayMethod");
	// ut1.put("returnType", "void");
	//
	// Map ut2 = new HashMap();
	// ut2.put("methodName", "deleteMethod");
	// ut2.put("returnType", "void");
	//
	// uts.add(ut1);
	// uts.add(ut2);
	//
	// function.put("UTs", uts);
	//
	// root.put("function", function);
	//
	// // merge data model and the template in the logs
	// Writer out = new OutputStreamWriter(System.out);
	// templateUtils.mergeDataModelWithTemplate(root, myTemplate, out);
	//
	// }

	@SuppressWarnings("rawtypes")
	@Test
	public void testBuildCrudManagedBean() throws Exception {

		CrudGeneration projectManagement = new CrudGeneration();

		/*
		 * OutputFile
		 */
		File myFile = new File("C:/myGeneratedBean.java");
		if (myFile.exists())
			FileUtils.forceDelete(myFile);

		myFile.createNewFile();

		Map dataModel = projectManagement.buildDataModel("voila",
				"myGeneratedBean", "Person",
				entityDto.getEntityAttributeList(),
				"am.ajf.web.controllers.test", entityDto.getEntityLibPackage());

		projectManagement.buildCrudManagedBean(myFile, dataModel);

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testBuildCrudXhtml() throws Exception {

		CrudGeneration projectManagement = new CrudGeneration();

		File myFile = new File("C:/myCrudXhtml.xhtml");
		if (!myFile.exists())
			myFile.createNewFile();

		Map dataModel = projectManagement.buildDataModel("voila",
				"myGeneratedBean", "Person",
				entityDto.getEntityAttributeList(),
				"am.ajf.web.controllers.test", entityDto.getEntityLibPackage());

		projectManagement.buildCrudXhtml(myFile, dataModel);

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

		JavaUtils javaUtils = new JavaUtils();
		entityDto.setEntityAttributeList(javaUtils
				.retrieveAttributeList(javaSource));

		entityDto.setEntityLibPackage("am.ajf.myProject.lib.model");

	}

}
