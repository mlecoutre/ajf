package am.ajf.web.utils;

import am.ajf.core.logger.LoggerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.slf4j.Logger;

/**
 * ManifestHelper utility class. Allow to extract data from Manifest to exploit
 * them on the web layer. (such as display the version).
 * 
 * @author E010925
 * 
 */
public class ManifestHelper {

	private Logger logger = LoggerFactory.getLogger(ManifestHelper.class);

	private Manifest manifest = null;

	/**
	 * 
	 * @param sc
	 *            ServletContext
	 */
	public ManifestHelper(ServletContext sc) {
		super();
		try {
			initialize(sc);
		} catch (IOException e) {
			logger.error("Unable to read '" + sc.getServletContextName()
					+ "' manifest.", e);
		}
	}

	private void initialize(ServletContext sc) throws IOException {
		String manifestPath = sc.getRealPath("/META-INF/MANIFEST.MF");
		if (null != manifestPath) {
			InputStream is = new FileInputStream(manifestPath);
			if (null != is) {
				manifest = new Manifest(is);
				is.close();
				is = null;
			}
		}
	}

	/**
	 * Get Attribute from the manifest
	 * 
	 * @param attributeName
	 *            to extract
	 * @return attribute value
	 */
	public String getAttribute(String attributeName) {

		if (null != manifest) {
			Attributes attrs = manifest.getMainAttributes();
			if (attrs.containsKey(attributeName)) {
				String value = null;
				try {
					value = attrs.getValue(attributeName);
				} catch (Exception e) {
					// Undefined attribute
					logger.warn(String
							.format("Impossible to get attribute %s from the manifest: %s",
									attributeName, e.getMessage()));
				}
				return value;
			} else {
				logger.warn(String.format(
						"Attribute %s is not declared in the manifest: %s",
						attributeName));
			}
		} else {
			logger.warn("Impossible to get the Manifest");
		}

		return null;
	}

	/**
	 * Get Attribute which contains in an specified entryName
	 * 
	 * @param entryName
	 *            the Entry
	 * @param attributeName
	 *            the attribute
	 * @return value of the attribute
	 */
	public String getAttribute(String entryName, String attributeName) {

		if (null != manifest) {
			Attributes attrs = manifest.getAttributes(entryName);
			if (null != attrs) {
				if (attrs.containsKey(attributeName)) {
					String value = null;
					try {
						value = attrs.getValue(attributeName);
					} catch (Exception e) {
						// Undefined attribute
						String msg = String.format("Can't get attribute %s for"
								+ " the entry %s of the manifest, %s",
								attributeName, entryName, e.getMessage());
						logger.warn(String.format(msg));
					}
					return value;
				} else {
					logger.warn(String.format(
							"Entry %s does not contain attribute %s.",
							entryName, attributeName));
				}
			}
		} else {
			logger.warn("Impossible to read the Manifest");
		}

		return null;
	}
}
