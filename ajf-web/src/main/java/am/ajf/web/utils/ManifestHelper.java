package am.ajf.web.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.slf4j.Logger;

import am.ajf.core.logger.LoggerFactory;

public class ManifestHelper {

	private final static Logger logger = LoggerFactory.getLogger(ManifestHelper.class);

	private Manifest manifest = null;

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
	 * 
	 * @param attributeName
	 * @return
	 */
	public String getAttribute(String attributeName) {

		if (null != manifest) {
			Attributes attrs = manifest.getMainAttributes();
			if (attrs.containsKey(attributeName)) {
				String value = null;
				try {
					value = attrs.getValue(attributeName);
				}
				catch (Exception e) {
					// Undefined attribute
				}
				return value;
			}
		}

		logger.debug("Unknow manifest attribute '" + attributeName + "'.");
		return null;
	}

	/**
	 * 
	 * @param entryName
	 * @param attributeName
	 * @return
	 */
	public String getAttribute(String entryName, String attributeName) {

		if (null != manifest) {
			Attributes attrs = manifest.getAttributes(entryName);
			if (null != attrs) {
				if (attrs.containsKey(attributeName)) {
					String value = null;
					try {
						value = attrs.getValue(attributeName);
					}
					catch (Exception e) {
						// Undefined attribute
					}
					return value;
				}
			}
		}

		logger.debug("Unknow manifest attribute '[" + entryName + "] "
				+ attributeName + "'.");
		return null;
	}
}
