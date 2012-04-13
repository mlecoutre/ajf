package am.ajf.forge.util;

import static am.ajf.forge.lib.ForgeConstants.DEFAULT_TEMPLATES_DIRECTORY;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Html Utilities (based on FreeMarker) For the moment totally commented as
 * there is a conflict between free marker and jboss forge
 * 
 * @author E019851
 * 
 */
public class TemplateUtils {

	private Configuration cfg;// = new Configuration(

	/**
	 * Default constructor (uses the Default template directory in the resource
	 * path of the class loader)
	 * 
	 * @throws Exception
	 */

	public TemplateUtils() throws Exception {

		setConfig("");

	}

	/**
	 * Constructor allowing a custom template directory as input
	 * 
	 * @param templateDirectory
	 * @throws Exception
	 */
	public TemplateUtils(String templateDirectory) throws Exception {

		setConfig(templateDirectory);
	}

	/**
	 * Set the configuration of FreeMarker. Either with a custom template
	 * directory path set as Input. Either (if the input directory path is an
	 * empty String), the default template directory in Resource of context
	 * classLoader will be used for this FreeMarker configuration.
	 * 
	 * @param templateDirectoryPath
	 * @throws Exception
	 */
	private void setConfig(String templateDirectoryPath) throws Exception {

		/* Create and adjust the FREEMARKER configuration */
		// Singleton pattern
		if (null == cfg)
			cfg = new Configuration();

		try {

			// If no directory path is set as input (empty), the default
			// template directory will be used (Resource path of the context
			// class loader
			File templateDirectoryFile;
			if ("".equals(templateDirectoryPath)
					|| templateDirectoryPath.isEmpty()) {

				templateDirectoryFile = locateTemplateDirectory();

			} else {

				templateDirectoryFile = new File(templateDirectoryPath);
			}

			// Log the directory containing the Templates
			System.out.println("templateDirectory: "
					+ templateDirectoryFile.getAbsolutePath());
			// Create the template loader
			FileTemplateLoader templateLoader = new FileTemplateLoader(
					templateDirectoryFile);
			// Set the template loader to the current configuration;
			cfg.setTemplateLoader(templateLoader);

		} catch (Exception e) {

			String message = "Error occured in the HTML Utils constructor";
			System.out.println(message.concat(": " + e.toString()));
			throw e;
		}

	}

	/**
	 * Locate the DEFAULT_TEMPLATES_DIRECTORY directory in the Resources of the
	 * class Loader
	 * 
	 * @return File corresponding to the template file resource
	 * @throws Exception
	 *             if default DEFAULT_TEMPLATES_DIRECTORY does not exist
	 * @throws URISyntaxException
	 */
	private File locateTemplateDirectory() throws Exception, URISyntaxException {

		// ClassLoader cls = Thread.currentThread().getContextClassLoader();
		// URL url = cls.getResource(DEFAULT_TEMPLATES_DIRECTORY);

		// URL url =
		// TemplateUtils.class.getResource(DEFAULT_TEMPLATES_DIRECTORY);
		URL url = TemplateUtils.class.getResource(DEFAULT_TEMPLATES_DIRECTORY);
		if (null == url) {

			String errorMessage = "Unable to find the resource directory "
					.concat(DEFAULT_TEMPLATES_DIRECTORY);
			System.out.println(errorMessage);
			throw new Exception(errorMessage);

		}

		File templateDirectory = new File(url.toURI());

		// log directory for verification
		System.out.println("template directory : "
				+ templateDirectory.getAbsolutePath());

		return templateDirectory;
	}

	/**
	 * Get the template from a Template name
	 * 
	 * @param templateName
	 * @return Template object corresponding to the FreeMarker template file set
	 *         as input
	 * @throws Exception
	 */
	public Template getTemplate(String templateName) throws Exception {

		try {

			return cfg.getTemplate(templateName);

		} catch (IOException e) {

			String errorMessage = "Error occured while locating the template "
					.concat(templateName + ". ");
			System.out.println(errorMessage.concat(e.toString()));
			throw e;

		}

	}

	/**
	 * Example employee data model
	 * 
	 * @param listOfHashMap
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map createEmployeeDataModel() {

		Map root = new HashMap();
		root.put("employee", "e01234");

		Map identityMap = new HashMap();
		root.put("identity", identityMap);
		identityMap.put("name", "Mr Vince");
		identityMap.put("age", 26);
		identityMap.put("sex", "Girl");

		Map adressMap = new HashMap();
		identityMap.put("address", adressMap);
		adressMap.put("city", "Dunkerque");
		adressMap.put("country", "France");

		return root;

	}

	/**
	 * Create a Map corresponding to a FreeMaker data model containing the
	 * application name. The aim of this data model is to be use for the Header
	 * template
	 * 
	 * @param applicationName
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map createHeaderDataModel(String applicationName) {

		Map headerDataModel = new HashMap();
		headerDataModel.put("appliName", applicationName);

		return headerDataModel;

	}

	/**
	 * Merge a FreeMaker template with a model and set write the result in the
	 * Writer set as param
	 * 
	 * @param dataModel
	 *            corresponding to the FreeMaker template
	 * @param template
	 *            adapted to the input data model
	 * @param writer
	 *            where to display the result of the merge
	 * @throws TemplateException
	 * @throws IOException
	 */
	public void mergeDataModelWithTemplate(
			@SuppressWarnings("rawtypes") Map dataModel, Template template,
			Writer writer) throws TemplateException, IOException {

		template.process(dataModel, writer);

	}
}
