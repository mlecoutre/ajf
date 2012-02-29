package am.ajf.web.controllers;

import javax.inject.Named;

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
	private String currentPage = DEFAULT_HELP_PATH + DEFAULT_HELP_VIEW;

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

		// TODO Check if contextual page exist

		// if not, just display index page

		return DEFAULT_HELP_PATH + DEFAULT_HELP_VIEW;
	}

	/**
	 * 
	 * @return currentPage
	 */
	public String getCurrentPage() {
		return currentPage;
	}

	/**
	 * 
	 * @param currentPage
	 *            the currentPage
	 */
	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}

}
