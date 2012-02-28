package am.ajf.web.controllers;

import javax.inject.Named;

@Named
@javax.enterprise.context.RequestScoped
public class HelpMBean {

	private static final String DEFAULT_HELP_PATH = "/shared/help";
	private static final String DEFAULT_HELP_VIEW = "/index.xhtml";
	private String currentPage = DEFAULT_HELP_PATH + DEFAULT_HELP_VIEW;

	public HelpMBean() {
		super();
	}

	/**
	 * Display contextual help
	 * 
	 * @return
	 */
	public String displayContextualHelp() {

		// TODO Check if contextual page exist

		// if not, just display index page

		return DEFAULT_HELP_PATH + DEFAULT_HELP_VIEW;
	}

	public String getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}

}
