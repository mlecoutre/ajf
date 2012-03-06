package am.ajf.web.controllers;

import am.ajf.core.logger.LoggerFactory;
import java.io.File;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.slf4j.Logger;

/**
 * Help MBean. Manage Contextual help for the application/
 * 
 * @author E010925
 * 
 */
@Named
@javax.enterprise.context.RequestScoped
public class HelpMBean {
	private static final String DEFAULT_HELP_PATH = "/shared/help";
	private static final String DEFAULT_HELP_VIEW = "/index.xhtml";

	private String context;

	private String currentPage = DEFAULT_HELP_PATH + DEFAULT_HELP_VIEW;

	private final Logger logger = LoggerFactory.getLogger(HelpMBean.class);

	private String viewId;

	/**
	 * Default constructor
	 */
	public HelpMBean() {
		super();
	}

	/**
	 * Display contextual help. Check if exist, if not, return the default
	 * help/index outcome.
	 * 
	 * @return outcome for specific help
	 */
	public String displayContextualHelp() {

		// if not, just display index page

		currentPage = String.format("%s%s", DEFAULT_HELP_PATH, viewId);
		logger.trace(String.format("generate help page view %s for view ",
				currentPage, viewId));

		// IF not exist, check if index exist
		String realPath = giveRealPath(currentPage);

		if (new File(realPath).exists()) {
			currentPage = context + currentPage;
		} else {
			// One index.xhtml page for the global P+ function
			String function = extractPalasFunction(viewId);
			currentPage = String.format("%s%s%s", DEFAULT_HELP_PATH, function,
					DEFAULT_HELP_VIEW);
			realPath = giveRealPath(currentPage);

			if (new File(realPath).exists()) {
				// add context for the web access
				currentPage = context + currentPage;
			} else {
				// go to the global shared/help/index.xhtml
				currentPage = String.format("%s%s", DEFAULT_HELP_PATH,
						DEFAULT_HELP_VIEW);
				realPath = giveRealPath(currentPage);
				if (new File(realPath).exists()) {
					currentPage = context + currentPage;
				} else {
					logger.error("Default shared/help/index.xhtml page cannot be found.");
					currentPage = "";
				}
			}
		}
		return currentPage;
	}

	/**
	 * return the function P+ and potentially its sub categories
	 * 
	 * @param viewId
	 *            global view returned by the face context
	 * @return function P+ and potentially its sub categories
	 */
	protected String extractPalasFunction(String viewId) {
		String[] parts = viewId.split("/");
		StringBuffer buff = new StringBuffer();
		// get all parts except the first and the last one.
		for (int i = 1; i < (parts.length - 1); i++) {
			buff.append("/").append(parts[i]);
		}
		return buff.toString();
	}

	public String getContext() {
		return context;
	}

	public String getCurrentPage() {
		return currentPage;
	}

	public String getViewId() {
		return viewId;
	}

	/**
	 * Check if page exist really on server
	 * 
	 * @param page
	 *            web relative url
	 * @return absolute path
	 */
	protected String giveRealPath(String page) {
		String res = FacesContext.getCurrentInstance().getExternalContext()
				.getRealPath(page);
		if (res == null) {
			return "";
		} else {
			return res;
		}
	}

	/**
	 * Init current page content
	 */
	@PostConstruct
	public void init() {

		context = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestContextPath();
		viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
		currentPage = displayContextualHelp();
	}

	public void setContext(String context) {
		this.context = context;
	}

	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

}
