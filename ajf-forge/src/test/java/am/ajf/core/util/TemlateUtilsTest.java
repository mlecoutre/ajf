package am.ajf.core.util;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.junit.Test;

import am.ajf.forge.util.TemplateUtils;
import freemarker.template.Template;

public class TemlateUtilsTest {

	@SuppressWarnings("rawtypes")
	@Test
	public void templateTest() throws Exception {

		System.out.println("** START templateTest");

		TemplateUtils templateUtils = new TemplateUtils("");
		System.out.println("template utils instanciated.");

		Template myTemplate = templateUtils.getTemplate("templateEmployee.ftl");
		System.out.println("templateEmployee.ftl located.");

		Map myDataModel = templateUtils.createEmployeeDataModel();
		System.out.println("employeeDataModel generated.");

		Writer out = new OutputStreamWriter(System.out);
		templateUtils.mergeDataModelWithTemplate(myDataModel, myTemplate, out);

	}
}
