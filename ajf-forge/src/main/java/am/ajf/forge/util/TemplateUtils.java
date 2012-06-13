package am.ajf.forge.util;

import static am.ajf.forge.lib.ForgeConstants.DEFAULT_TEMPLATES_DIRECTORY_ZIP;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.Map;

import javax.inject.Singleton;

import org.apache.commons.io.FileUtils;

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
@Singleton
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

		File templateDirectoryFile = null;
		try {

			// If no directory path is set as input (empty), the default
			// template directory will be used (Resource path of the context
			// class loader
			if ("".equals(templateDirectoryPath)
					|| templateDirectoryPath.isEmpty()) {

				templateDirectoryFile = locateTemplateDirectory();

			} else {

				templateDirectoryFile = new File(templateDirectoryPath);
			}

			// Create the template loader
			FileTemplateLoader templateLoader = new FileTemplateLoader(
					templateDirectoryFile);

			// FileUtils.forceDelete(templateDirectoryFile);
			templateDirectoryFile = null;
			// Set the template loader to the current configuration;
			cfg.setTemplateLoader(templateLoader);

		} catch (Exception e) {

			String message = "Error occured in the HTML Utils constructor";
			System.out.println(message.concat(": " + e.toString()));

			// Remove templateDirectoryFile which is a temp directory
			if (null != templateDirectoryFile) {

				if (templateDirectoryFile.exists())
					FileUtils.forceDelete(templateDirectoryFile);

				templateDirectoryFile = null;
			}
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

		File templateDirectory = new File(FileUtils.getTempDirectoryPath()
				.concat("/FreeMarkerTemplatesTmp"));

		// System.out.println("**DEBUG - Template TempDirectory : "
		// + templateDirectory.getAbsolutePath());

		ExtractionUtils.unzipFile(DEFAULT_TEMPLATES_DIRECTORY_ZIP,
				templateDirectory);

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
