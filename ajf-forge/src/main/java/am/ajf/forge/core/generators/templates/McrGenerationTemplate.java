package am.ajf.forge.core.generators.templates;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
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

			// function.put("entityName", entityName);
			Map entity = new HashMap();
			entity.put("name", entityName);
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
			// init:
			function.put("addUT", "");
			function.put("deleteUT", "");
			function.put("addFlag", "false");
			function.put("deleteFlag", "false");
			for (String ut : uts) {

				// check if an Add (or create) UT has been asked
				if (ut.startsWith("add" + WordUtils.capitalize(entityName))
						|| ut.startsWith("create"
								+ WordUtils.capitalize(entityName))) {
					function.put("addFlag", "true");
					function.put("addUT", ut);

				} else if (ut.startsWith("remove"
						+ WordUtils.capitalize(entityName))
						|| ut.startsWith("delete"
								+ WordUtils.capitalize(entityName))) {

					function.put("deleteFlag", "true");
					function.put("deleteUT", ut);
				}

			}

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

		} catch (Exception e) {

			System.err.println("Error occured in buildCrudXhtml : ");
			e.printStackTrace();
			throw e;
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

		} catch (Exception e) {

			System.err
					.println("Error occured in buildBusinessDelegateInterface : ");
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * Build a pollicy template java class
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

		} catch (Exception e) {

			System.err
					.println("Error occured in buildBusinessDelegateInterface : ");
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * 
	 * @param functionName
	 * @param ut
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String buildManagedBeanMethod(String functionName, String ut)
			throws Exception {

		Template myTemplate = loadTemplate(MBEAN_METHOD_TEMPLATE);

		Map dataModelMap = new HashMap();

		// package that contain the BD interfaces in the lib project
		dataModelMap.put("functionName", functionName);
		dataModelMap.put("ut", ut);
		dataModelMap.put("unCapitalizeFirst", new UnCapitalizeFirst());
		dataModelMap.put("capitalizeFirst", new CapitalizeFirst());

		Writer writer = new StringWriter();

		templateUtils.mergeDataModelWithTemplate(dataModelMap, myTemplate,
				writer);

		return writer.toString();
	}

	/**
	 * 
	 * @param functionName
	 * @param ut
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String buildPolicyMethod(String ut) throws Exception {

		Template myTemplate = loadTemplate(POLICY_METHOD_TEMPLATE);

		Map dataModelMap = new HashMap();

		// package that contain the BD interfaces in the lib project
		dataModelMap.put("ut", ut);
		dataModelMap.put("unCapitalizeFirst", new UnCapitalizeFirst());
		dataModelMap.put("capitalizeFirst", new CapitalizeFirst());

		Writer writer = new StringWriter();

		templateUtils.mergeDataModelWithTemplate(dataModelMap, myTemplate,
				writer);

		return writer.toString();
	}

	// /**
	// *
	// * Generate the common part of the data model. This data model is commun
	// to
	// * diffrent templates, that's why it is isolated
	// *
	// * @param globalProjectName
	// * @param functionName
	// * @param entityName
	// * @param entityAttributes
	// * @param javaPackage
	// * @param entityLibPackage
	// * @return Map data model
	// */
	// @SuppressWarnings({ "rawtypes", "unchecked" })
	// public Map buildDataModel(String globalProjectName, String functionName,
	// String entityName, List<String> entityAttributes,
	// String javaPackage, String entityLibPackage) {
	//
	// // Generate an my data model
	// Map root = new HashMap();
	//
	// root.put("projectGlobalName", globalProjectName);
	//
	// Map function = new HashMap();
	// function.put("MbeanName", functionName);
	// function.put("package", javaPackage);
	// // function.put("entityName", entityName);
	// Map entity = new HashMap();
	// entity.put("name", entityName);
	// entity.put("libPackage", entityLibPackage);
	//
	// SimpleSequence attributeSequence = new SimpleSequence();
	// for (String attribute : entityAttributes) {
	// attributeSequence.add(attribute);
	// }
	// entity.put("attributes", attributeSequence);
	//
	// function.put("entity", entity);
	// function.put("capitalizeFirst", new CapitalizeFirst());
	//
	// root.put("function", function);
	// root.put("unCapitalizeFirst", new UnCapitalizeFirst());
	// root.put("capitalizeFirst", new CapitalizeFirst());
	//
	// return root;
	//
	// }

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
