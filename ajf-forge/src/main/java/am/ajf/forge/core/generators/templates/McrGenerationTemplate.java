package am.ajf.forge.core.generators.templates;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.apache.commons.lang3.text.WordUtils;

import am.ajf.forge.util.TemplateUtils;
import freemarker.template.SimpleSequence;
import freemarker.template.Template;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

@Singleton
public class McrGenerationTemplate {

	private static final String MBEAN_TEMPLATE = "ManagedBean.ftl";
	private static final String XHTML_TEMPLATE = "Xhtml.ftl";
	private static final String BUSINESS_DELEGATE_TEMPLATE = "BusinessDelegate.ftl";
	private static final String BUSINESS_POLICY_TEMPLATE = "Policy.ftl";
	private static final String MBEAN_METHOD_TEMPLATE = "ManagedBeanMethod.ftl";
	private static final String POLICY_METHOD_TEMPLATE = "PolicyMethod.ftl";

	TemplateUtils templateUtils;

	public McrGenerationTemplate() {
		super();
	}

	/**
	 * Generate a Managed Bean java class according to the FreeMarker template
	 * corresponding to the input data model
	 * 
	 * @param managedBeanFile
	 * @param globalProjectName
	 * @param functionName
	 * @param entityName
	 * @param entityAttributes
	 * @param managedBeanPackage
	 * @param libBDPackage
	 * @param libDTOPackage
	 * @param entityLibPackage
	 * @param uts
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void buildManagedBean(File managedBeanFile,
			String globalProjectName, String functionName, String entityName,
			List<String> entityAttributes, String managedBeanPackage,
			String libBDPackage, String libDTOPackage, String entityLibPackage,
			List<String> uts) throws Exception {

		try {

			FileOutputStream fos = new FileOutputStream(managedBeanFile);
			Writer writer = new OutputStreamWriter(fos);

			Template myTemplate = loadTemplate(MBEAN_TEMPLATE);

			// Generate a data model
			Map dataModelMap = new HashMap();

			dataModelMap.put("projectGlobalName", globalProjectName);

			Map function = new HashMap();
			function.put("name", functionName);
			function.put("package", managedBeanPackage);

			function.put("libBDPackage", libBDPackage);
			function.put("libDTOPackage", libDTOPackage);

			// Set special UTs verification
			setSpecialUtVerifications(WordUtils.capitalize(entityName), uts,
					function);

			// function.put("entityName", entityName);
			Map entity = new HashMap();
			entity.put("name", WordUtils.capitalize(entityName));
			entity.put("libPackage", entityLibPackage);

			SimpleSequence attributeSequence = new SimpleSequence();
			for (String attribute : entityAttributes) {
				attributeSequence.add(attribute);
			}
			entity.put("attributes", attributeSequence);

			// UT List
			SimpleSequence utSequence = new SimpleSequence();
			for (String ut : uts) {
				utSequence.add(ut);
			}
			function.put("UTs", utSequence);

			function.put("entity", entity);

			dataModelMap.put("function", function);
			dataModelMap.put("unCapitalizeFirst", new UnCapitalizeFirst());
			dataModelMap.put("capitalizeFirst", new CapitalizeFirst());

			// merge data model and the template in the logs
			// Writer out = new OutputStreamWriter(System.out);
			templateUtils.mergeDataModelWithTemplate(dataModelMap, myTemplate,
					writer);

			fos.close();
			fos = null;
			writer.close();
			writer = null;

		} catch (Exception e) {

			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Build the CRUD xhmtl corresponding to input data model, and the
	 * freemarker template. The xhtml file is also set as input
	 * 
	 * @param xhtmlFile
	 * @param functionName
	 * @param entityName
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buildXhtml(File xhtmlFile, String globalProjectName,
			String functionName, String entityName,
			List<String> entityAttributes, String javaPackage,
			String entityLibPackage, List<String> uts) throws Exception {

		try {
			FileOutputStream fos = new FileOutputStream(xhtmlFile);
			Writer writer = new OutputStreamWriter(fos);

			Template myTemplate = loadTemplate(XHTML_TEMPLATE);

			// Generate a data model
			Map dataModelMap = new HashMap();

			dataModelMap.put("projectGlobalName", globalProjectName);

			Map function = new HashMap();
			function.put("MbeanName", functionName);
			function.put("package", javaPackage);
			// function.put("entityName", entityName);
			Map entity = new HashMap();
			entity.put("name", entityName);
			entity.put("libPackage", entityLibPackage);

			SimpleSequence attributeSequence = new SimpleSequence();
			for (String attribute : entityAttributes) {
				attributeSequence.add(attribute);
			}
			entity.put("attributes", attributeSequence);

			/*
			 * need to find speacial UT for special XHTML sub part
			 */
			setSpecialUtVerifications(entityName, uts, function);

			function.put("entity", entity);

			dataModelMap.put("function", function);
			dataModelMap.put("unCapitalizeFirst", new UnCapitalizeFirst());
			dataModelMap.put("capitalizeFirst", new CapitalizeFirst());

			// add setToEl to data Model (only needed for building xhtmlFile)
			dataModelMap.put("setToEl", new TransformToEL());

			// merge data model and the template in the logs
			// Writer out = new OutputStreamWriter(System.out);
			templateUtils.mergeDataModelWithTemplate(dataModelMap, myTemplate,
					writer);

			fos.close();
			fos = null;
			writer.close();
			writer = null;

		} catch (Exception e) {

			System.err.println("Error occured in buildCrudXhtml : ");
			e.printStackTrace();
			throw e;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setSpecialUtVerifications(String entityName, List<String> uts,
			Map function) {
		// init:
		function.put("addUT", "");
		function.put("deleteUT", "");
		function.put("listUT", "");
		function.put("addFlag", "false");
		function.put("deleteFlag", "false");
		function.put("listFlag", "false");

		for (String ut : uts) {

			// check if special create UT has been asked
			if (ut.startsWith("create" + WordUtils.capitalize(entityName))) {
				function.put("addFlag", "true");
				function.put("addUT", ut);

				// check if special delete UT has been asked
			} else if (ut.startsWith("delete"
					+ WordUtils.capitalize(entityName))) {

				function.put("deleteFlag", "true");
				function.put("deleteUT", ut);

				// check if special list UT has been asked
			} else if (ut.startsWith("list" + WordUtils.capitalize(entityName))) {

				function.put("listFlag", "true");
				function.put("listUT", ut);
			}

		}
	}

	/**
	 * Build the function BD interface corresponding to template and inputs
	 * 
	 * @param file
	 * @param libBusinessPackage
	 * @param functionName
	 * @param uts
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void buildBusinessDelegateInterface(File file,
			String libBusinessPackage, String functionName, List<String> uts,
			String dtoPackage) throws Exception {

		try {
			FileOutputStream fos = new FileOutputStream(file);
			Writer writer = new OutputStreamWriter(fos);

			Template myTemplate = loadTemplate(BUSINESS_DELEGATE_TEMPLATE);

			Map dataModelMap = new HashMap();

			// package that contain the BD interfaces in the lib project
			dataModelMap.put("libBusinessPackage", libBusinessPackage);
			dataModelMap.put("libDtoPackage", dtoPackage);
			dataModelMap.put("unCapitalizeFirst", new UnCapitalizeFirst());
			dataModelMap.put("capitalizeFirst", new CapitalizeFirst());

			// function beeing generated
			Map function = new HashMap();
			function.put("name", functionName);

			// UT List
			SimpleSequence utSequence = new SimpleSequence();
			for (String ut : uts) {
				utSequence.add(ut);
			}
			function.put("UTs", utSequence);

			dataModelMap.put("function", function);

			templateUtils.mergeDataModelWithTemplate(dataModelMap, myTemplate,
					writer);

			fos.close();
			fos = null;
			writer.close();
			writer = null;

		} catch (Exception e) {

			System.err
					.println("Error occured in buildBusinessDelegateInterface : ");
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * Build a policy template java class
	 * 
	 * @param policyFile
	 * @param functionName
	 * @param uts
	 * @param libBusinessPackage
	 * @param dtoPackage
	 * @param corePolicyPackage
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void buildPolicy(File policyFile, String functionName,
			List<String> uts, String libBusinessPackage, String dtoPackage,
			String corePolicyPackage) throws Exception {

		try {
			FileOutputStream fos = new FileOutputStream(policyFile);
			Writer writer = new OutputStreamWriter(fos);

			Template myTemplate = loadTemplate(BUSINESS_POLICY_TEMPLATE);

			Map dataModelMap = new HashMap();

			// package that contain the BD interfaces in the lib project
			dataModelMap.put("corePolicyPackage", corePolicyPackage);
			dataModelMap.put("libBusinessPackage", libBusinessPackage);
			dataModelMap.put("libBusinessDtoPackage", dtoPackage);
			dataModelMap.put("unCapitalizeFirst", new UnCapitalizeFirst());
			dataModelMap.put("capitalizeFirst", new CapitalizeFirst());

			// function beeing generated
			Map function = new HashMap();
			function.put("name", functionName);

			// UT List
			SimpleSequence utSequence = new SimpleSequence();
			for (String ut : uts) {
				utSequence.add(ut);
			}
			function.put("UTs", utSequence);
			dataModelMap.put("function", function);

			templateUtils.mergeDataModelWithTemplate(dataModelMap, myTemplate,
					writer);

			fos.close();
			fos = null;
			writer.close();
			writer = null;

		} catch (Exception e) {

			System.err
					.println("Error occured in buildBusinessDelegateInterface : ");
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * Build a File containing managed bean class with templated managed bean
	 * method. Use this method to add a Unit task in a managed bean
	 * 
	 * @param tmpFile
	 *            temporary file where the template will be stored
	 * @param functionName
	 * @param entityName
	 * @param ut
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buildManagedBeanMethod(File tmpFile, String functionName,
			String entityName, String ut, String libDtoPackage)
			throws Exception {

		Template myTemplate = loadTemplate(MBEAN_METHOD_TEMPLATE);

		Map dataModelMap = new HashMap();

		// package that contain the BD interfaces in the lib project
		dataModelMap.put("functionName", functionName);
		dataModelMap.put("ut", ut);
		dataModelMap.put("unCapitalizeFirst", new UnCapitalizeFirst());
		dataModelMap.put("capitalizeFirst", new CapitalizeFirst());

		Map function = new HashMap();
		function.put("functionName", functionName);
		function.put("libDTOPackage", libDtoPackage);
		function.put("entityName", WordUtils.capitalize(entityName));

		// Set the input ut in a list to use setSpecialUtVerifications
		List<String> uts = new ArrayList<String>();
		uts.add(ut);
		setSpecialUtVerifications(entityName, uts, function);

		dataModelMap.put("function", function);

		// check existence of tmp file - create it if needed
		if (!tmpFile.getParentFile().exists())
			tmpFile.getParentFile().mkdirs();

		if (!tmpFile.exists())
			tmpFile.createNewFile();

		FileOutputStream fos = new FileOutputStream(tmpFile);
		Writer writer = new OutputStreamWriter(fos);

		templateUtils.mergeDataModelWithTemplate(dataModelMap, myTemplate,
				writer);

		fos.close();
		fos = null;
		writer.close();
		writer = null;

		// return tmpFile;

	}

	/**
	 * Build a File containing managed bean class with templated Policy method.
	 * Use this method to add a Unit task in a Policy
	 * 
	 * @param tmpFile
	 * @param ut
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buildPolicyMethod(File tmpFile, String ut) throws Exception {

		Template myTemplate = loadTemplate(POLICY_METHOD_TEMPLATE);

		Map dataModelMap = new HashMap();

		// package that contain the BD interfaces in the lib project
		dataModelMap.put("ut", ut);
		dataModelMap.put("unCapitalizeFirst", new UnCapitalizeFirst());
		dataModelMap.put("capitalizeFirst", new CapitalizeFirst());

		if (!tmpFile.getParentFile().exists())
			tmpFile.getParentFile().mkdirs();

		if (!tmpFile.exists())
			tmpFile.createNewFile();

		FileOutputStream fos = new FileOutputStream(tmpFile);
		Writer writer = new OutputStreamWriter(fos);

		templateUtils.mergeDataModelWithTemplate(dataModelMap, myTemplate,
				writer);

		fos.close();
		fos = null;
		writer.close();
		writer = null;

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

class CapitalizeFirst implements TemplateMethodModel {

	@SuppressWarnings("rawtypes")
	public String exec(List args) throws TemplateModelException {
		if (args.size() != 1) {
			throw new TemplateModelException("Wrong arguments");
		}
		return WordUtils.capitalize((String) args.get(0));

	}
}

class UnCapitalizeFirst implements TemplateMethodModel {

	@SuppressWarnings("rawtypes")
	public String exec(List args) throws TemplateModelException {
		if (args.size() != 1) {
			throw new TemplateModelException("Wrong arguments");
		}
		return WordUtils.uncapitalize((String) args.get(0));

	}
}
