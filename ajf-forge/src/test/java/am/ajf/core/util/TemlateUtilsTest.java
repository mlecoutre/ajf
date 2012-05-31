package am.ajf.core.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import am.ajf.forge.core.CrudGeneration;
import am.ajf.forge.util.TemplateUtils;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleSequence;
import freemarker.template.Template;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;

/**
 * 
 * Test a complete templating Test using FreeMarker. All the use of FreeMarker
 * API is done in templateUtils. This whole test case is commented as for the
 * moment FreeMarker make the forge plugin fail...
 * 
 * @author E019851
 */
public class TemlateUtilsTest {

	private static final String FREEMARKER_TEMPLATE_NAME = "templateEmployee.ftl";

	@SuppressWarnings("rawtypes")
	@Test
	public void templateTest() {

		System.out.println("** START templateTest");
		boolean hasErrorOcccured = false;

		try {

			// Instanciate TemplateUtils and freeMarker with default template
			// dir
			// (in resource)
			TemplateUtils templateUtils = new TemplateUtils();
			System.out.println("template utils instanciated.");

			// Find the a template in the project resources
			Template myTemplate = templateUtils
					.getTemplate(FREEMARKER_TEMPLATE_NAME);
			System.out.println("template".concat(FREEMARKER_TEMPLATE_NAME)
					.concat(" located."));

			// Generate an Employee data model
			Map myDataModel = templateUtils.createEmployeeDataModel();
			System.out.println("employeeDataModel generated.");

			// merge data model and the template in the logs
			Writer out = new OutputStreamWriter(System.out);
			templateUtils.mergeDataModelWithTemplate(myDataModel, myTemplate,
					out);

		} catch (Exception e) {

			System.out.println("Error occured during templateTest : ".concat(e
					.toString()));
			hasErrorOcccured = true;

		}

		// The condition of success of this test is that no Exception should be
		// thrown
		assertTrue("No exception should occured", !hasErrorOcccured);

		System.out.println("** END templateTest");

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

	@Test
	public void testBuildCrudManagedBean() throws Exception {

		CrudGeneration projectManagement = new CrudGeneration();

		File myFile = new File("C:/myGeneratedBean.java");
		if (!myFile.exists())
			myFile.createNewFile();

		projectManagement.buildCrudManagedBean(myFile, "myGeneratedBean",
				"Employee", "am.ajf.web.controllers.test");

	}

	@Test
	public void testBuildCrudXhtml() throws Exception {

		CrudGeneration projectManagement = new CrudGeneration();

		File myFile = new File("C:/myCrudXhtml.xhtml");
		if (!myFile.exists())
			myFile.createNewFile();

		projectManagement.buildCrudXhtml(myFile, "myGeneratedBean", "Employee");

	}
}
