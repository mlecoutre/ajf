package am.ajf.web;

import java.net.URL;
import javax.faces.view.facelets.ResourceResolver;

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

	private static final String META_INF_RESOURCES = "/META-INF/resources";
	private final ResourceResolver parent;
	private final String basePath;

	/**
	 * Constructor call by JSF2
	 * 
	 * @param parent
	 *            default ResourceResolver
	 */
	public FaceletsResourceResolver(ResourceResolver parent) {
		this.parent = parent;
		this.basePath = META_INF_RESOURCES;
	}

	@Override
	public URL resolveUrl(String path) {
		// Resolves from WAR, default behaviour
		URL url = parent.resolveUrl(path);

		if (url == null) {
			url = getClass().getResource(basePath + path); // Resolves from JAR.
		}

		return url;
	}

}