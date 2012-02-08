package am.ajf.web.controllers;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean
public class HabilitationMBean {

	private transient static Logger logger = LoggerFactory
			.getLogger(HabilitationMBean.class);

	// DUN-GG-APP-WS_MCR_ADM-R
	public static String ROLE_ADMIN = "admin";
	public static String ROLE_POWERUSER = "poweruser";
	public static String ROLE_USER = "user";

	public static String UNREFERENCED_USER = "UnRefrenced User";

	/**
	 * Method to be used with at least version 2.2 of EL. Return true if the
	 * user has the rights of the Role set as input.
	 * 
	 * @param role
	 * @return boolean
	 */
	public boolean isAllowed(String role) {

		// if the current user belongs to the role set in input, a 'true' value
		// is returned

		return FacesContext.getCurrentInstance().getExternalContext()
				.isUserInRole(role);

	}

	/**
	 * Return true if the user is logged in (what ever the role)
	 * 
	 * 
	 * @return
	 */
	public boolean getIsLogIn() {

		// if the current user belongs to the role set in input, a 'true' value
		// is returned
		String userName = getUserName();
		if (null == userName || UNREFERENCED_USER.equals(userName)) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * Log out the session of the application
	 * 
	 * @param event
	 */
	public void logout() {

		String userName = FacesContext.getCurrentInstance()
				.getExternalContext().getUserPrincipal().getName();

		logger.debug("Logging out user : " + userName + "...");

		FacesContext.getCurrentInstance().getExternalContext()
				.invalidateSession();

	}

	/**
	 * Retrieve the session username
	 * 
	 * @return String user name
	 */
	public String getUserName() {

		try {
			String userName = FacesContext.getCurrentInstance()
					.getExternalContext().getUserPrincipal().getName();

			return userName;
		} catch (Exception ex) {
			// Do nothing

		}
		return UNREFERENCED_USER;

	}

	/**
	 * Return true if the user is in the ADMIN role
	 * 
	 * @return boolean
	 */
	public boolean getIsAdmin() {

		return FacesContext.getCurrentInstance().getExternalContext()
				.isUserInRole(ROLE_ADMIN);

	}

	/**
	 * Return true if the user is in the POWER USER role
	 * 
	 * @return boolean
	 */
	public boolean getIsPowerUser() {
		return FacesContext.getCurrentInstance().getExternalContext()
				.isUserInRole(ROLE_POWERUSER);
	}

	/**
	 * Return true if the user is in the USER role
	 * 
	 * @return boolean
	 */
	public boolean getIsUser() {

		return FacesContext.getCurrentInstance().getExternalContext()
				.isUserInRole(ROLE_USER);
	}

}
