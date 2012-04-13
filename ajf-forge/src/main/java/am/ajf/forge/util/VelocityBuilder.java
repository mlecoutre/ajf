package am.ajf.forge.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class VelocityBuilder {

	private final static Object initToken = new Object();
	private static boolean initialized = false;

	private VelocityContext velocityContext = null;

	/**
	 * initialize the Velocity Engine
	 */
	private static void init() {

		if (initialized)
			return;

		synchronized (initToken) {
			if (!initialized) {
				/* initialize the velocity engine */
				Properties vProperties = new Properties();
				vProperties.setProperty(Velocity.RESOURCE_LOADER, "class");
				vProperties
						.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
				Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM,
						new VelocitySlf4JLogSystem());
				Velocity.init(vProperties);
			}
		}
	}

	/**
	 * default builder
	 */
	public VelocityBuilder() {
		super();
		init();
	}

	/**
	 * @return the VelocityContext
	 */
	public VelocityContext getContext() {

		if (null == this.velocityContext)
			this.velocityContext = new VelocityContext();
		return this.velocityContext;

	}

	/**
	 * @param velocityTemplateName
	 * @param velocityContext
	 * @param targetFilePath
	 * @throws IOException
	 */
	public void merge(String velocityTemplateName, String targetFilePath)
			throws IOException {

		Template velocityTemplate = retrieveTemplate(velocityTemplateName);

		File targetFile = new File(targetFilePath);
		Writer writer = null;
		try {
			writer = new FileWriter(targetFile);
			velocityTemplate.merge(this.velocityContext, writer);
			writer.flush();
		} finally {
			if (null != writer) {
				try {
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				writer = null;
			}
		}

	}

	/**
	 * 
	 * @param velocityTemplateName
	 * @param writer
	 * @throws IOException
	 */
	public void merge(String velocityTemplateName, Writer writer)
			throws IOException {

		Template velocityTemplate = retrieveTemplate(velocityTemplateName);
		velocityTemplate.merge(this.velocityContext, writer);
		writer.flush();

	}

	/**
	 * @param velocityTemplateName
	 * @return the VelocityTemplate
	 */
	private Template retrieveTemplate(String velocityTemplateName) {
		Template t = Velocity.getTemplate(velocityTemplateName);
		return t;
	}

}
