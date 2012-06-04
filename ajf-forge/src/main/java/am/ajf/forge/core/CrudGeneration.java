package am.ajf.forge.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import am.ajf.forge.util.TemplateUtils;
import freemarker.template.SimpleSequence;
import freemarker.template.Template;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

@Singleton
public class CrudGeneration {

	private static final String CRUD_MBEAN_TEMPLATE = "CrudMBean.ftl";
	private static final String CRUD_XHTML_TEMPLATE = "CrudXhtml.ftl";

	TemplateUtils templateUtils;

	/**
	 * Generate a Managed Bean java class according to the FreeMarker template
	 * corresponding to the input functionName, entityName and java package
	 * 
	 * @param managedBeanFile
	 * @param functionName
	 * @param entityName
	 * @param javaPackage
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public void buildCrudManagedBean(File managedBeanFile, Map dataModelMap)
			throws Exception {

		try {

			FileOutputStream fos = new FileOutputStream(managedBeanFile);
			Writer writer = new OutputStreamWriter(fos);

			Template myTemplate = loadTemplate(CRUD_MBEAN_TEMPLATE);

			// merge data model and the template in the logs
			// Writer out = new OutputStreamWriter(System.out);
			templateUtils.mergeDataModelWithTemplate(dataModelMap, myTemplate,
					writer);

		} catch (Exception e) {

			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 
	 * @param xhtmlFile
	 * @param functionName
	 * @param entityName
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buildCrudXhtml(File xhtmlFile, Map dataModelMap)
			throws Exception {

		try {
			FileOutputStream fos = new FileOutputStream(xhtmlFile);
			Writer writer = new OutputStreamWriter(fos);

			Template myTemplate = loadTemplate(CRUD_XHTML_TEMPLATE);

			// add setToEl to data Model (only needed for building xhtmlFile)
			dataModelMap.put("setToEl", new TransformToEL());

			// merge data model and the template in the logs
			// Writer out = new OutputStreamWriter(System.out);
			templateUtils.mergeDataModelWithTemplate(dataModelMap, myTemplate,
					writer);

		} catch (Exception e) {

			System.err.println("Error occured in buildCrudXhtml : ");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 
	 * @param globalProjectName
	 * @param functionName
	 * @param entityName
	 * @param entityAttributes
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map buildDataModel(String globalProjectName, String functionName,
			String entityName, List<String> entityAttributes, String javaPackage) {

		// Generate an my data model
		Map root = new HashMap();

		root.put("projectGlobalName", globalProjectName);

		Map function = new HashMap();
		function.put("MbeanName", functionName);
		function.put("package", javaPackage);
		// function.put("entityName", entityName);
		Map entity = new HashMap();
		entity.put("name", entityName);

		SimpleSequence attributeSequence = new SimpleSequence();
		for (String attribute : entityAttributes) {
			attributeSequence.add(attribute);
		}
		entity.put("attributes", attributeSequence);

		function.put("entity", entity);

		root.put("function", function);

		return root;

	}

	/**
	 * Return a reference to the FreeMarker template corresponding to the input
	 * template name
	 * 
	 * @param templateName
	 * @return Template
	 * @throws Exception
	 */
	private Template loadTemplate(String templateName) throws Exception {

		if (null == templateUtils) {
			templateUtils = new TemplateUtils();
		}

		// Find the a template in the project resources
		Template myTemplate = templateUtils.getTemplate(templateName);
		// System.out.println("template".concat(templateName).concat(" located."));

		return myTemplate;

	}
}

class TransformToEL implements TemplateMethodModel {

	@SuppressWarnings("rawtypes")
	public String exec(List args) throws TemplateModelException {
		if (args.size() == 0) {
			throw new TemplateModelException("Wrong arguments");
		}

		String returnValue = String.valueOf(args.get(0));
		int i = 2;
		for (i = 2; i <= args.size(); i++) {
			returnValue = returnValue + "." + args.get(i - 1);
		}

		return "#{".concat(returnValue).concat("}");
	}
}