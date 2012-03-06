package am.ajf.web.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement AboutMBean popup
 * 
 * @author E010925
 * 
 */
@Named
@RequestScoped
public class AboutMBean implements Serializable {

	private static final String IMPL_TITLE = "Implementation-Title";
	private static final String IMPL_VENDOR = "Implementation-Vendor";
	private static final String IMPL_VERSION = "Implementation-Version";
	/**
	 * 
	 * UID
	 */
	private static final long serialVersionUID = 1L;

	private transient Logger logger = LoggerFactory
			.getLogger(AboutMBeanTest.class);

	// private List<KeyValueVO> keyValueList = new ArrayList<KeyValueVO>();
	private String implementationTitle, implementationVendor,
			implementationVersion;

	/**
	 * Default constructor
	 */
	public AboutMBean() {
		super();
	}

	/**
	 * Initialize the AboutMBean info
	 */
	@PostConstruct
	public void init() {

		ServletContext ct = (ServletContext) FacesContext.getCurrentInstance()
				.getExternalContext().getContext();

		// Path of the context
		String manifestPath = ct.getRealPath("/");

		// modify the path in order to be usable
		manifestPath = manifestPath.replace("\\", "/");
		manifestPath = manifestPath.replace("//", "/");

		// Add a "/" if it's not
		if (!manifestPath.endsWith("/")) {
			manifestPath = manifestPath.concat("/");
		}
		// Use the manifest complet path
		manifestPath = manifestPath.concat("META-INF/MANIFEST.MF");

		InputStream in = null;
		try {
			File manifestFile = new File(manifestPath);
			if (manifestFile.exists()) {
				in = new FileInputStream(manifestFile);
				Manifest manifest = new Manifest(in);

				Attributes attrs = manifest.getMainAttributes();

				implementationTitle = attrs.getValue(IMPL_TITLE);
				implementationVendor = attrs.getValue(IMPL_VENDOR);
				implementationVersion = attrs.getValue(IMPL_VERSION);
			} else {
				logger.warn("Impossible to get  'manifest file'");
			}

		} catch (FileNotFoundException e) {
			logger.error("Unable to find the 'Manifest' file.", e);
		} catch (IOException e) {
			logger.error("Unable to read the 'Manifest' file.", e);
		} finally {
			in = null;
		}

	}

	// @PreDestroy
	// public void terminate() {
	// implementationTitle = null;
	// implementationVendor = null;
	// implementationVersion = null;
	// }

	/**
	 * 
	 * @return ImplementationTitle
	 */
	public String getImplementationTitle() {
		return implementationTitle;
	}

	/**
	 * 
	 * @param implementationTitle
	 *            implementationTitle
	 */
	public void setImplementationTitle(String implementationTitle) {
		this.implementationTitle = implementationTitle;
	}

	/**
	 * 
	 * @return implementationVendor
	 */
	public String getImplementationVendor() {
		return implementationVendor;
	}

	/**
	 * 
	 * @param implementationVendor
	 *            implementationVendor
	 */
	public void setImplementationVendor(String implementationVendor) {
		this.implementationVendor = implementationVendor;
	}

	/**
	 * 
	 * @return implementationVersion
	 */
	public String getImplementationVersion() {
		return implementationVersion;
	}

	/**
	 * 
	 * @param implementationVersion
	 *            implementationVersion
	 */
	public void setImplementationVersion(String implementationVersion) {
		this.implementationVersion = implementationVersion;
	}

}
