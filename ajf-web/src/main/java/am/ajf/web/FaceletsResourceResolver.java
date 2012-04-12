package am.ajf.web;

import am.ajf.core.logger.LoggerFactory;
import java.net.URL;
import javax.faces.view.facelets.ResourceResolver;
import org.slf4j.Logger;

/**
 * Allow to resolve resources such xhtml files in JAR You need to declare the
 * resolver in the web.xml by this way:
 * 
 * <pre>
 * <context-param>
 *   <param-name>javax.faces.FACELETS_RESOURCE_RESOLVER</param-name>
 *   <param-value>am.ajf.web.FaceletsResourceResolver</param-value>
 * </context-param>
 * 
 * </pre>
 * 
 * @author E010925
 * 
 */
public class FaceletsResourceResolver extends ResourceResolver {

	private static final String EXT_RESOURCES = "/ext/";
	private final ResourceResolver parent;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Constructor call by JSF2
	 * 
	 * @param parent
	 *            default ResourceResolver
	 */
	public FaceletsResourceResolver(ResourceResolver parent) {
		this.parent = parent;
	}

	@Override
	public URL resolveUrl(String path) {
		logger.trace("FaceletsResourceResolver:" + path);
		if ((path != null) && path.startsWith(EXT_RESOURCES)) {
			final String resource = path.substring(EXT_RESOURCES.length()); // remove
																			// '/'
			final URL url = getClass().getClassLoader().getResource(resource);
			return url;
		} else {
			return parent.resolveUrl(path);
		}
	}

}