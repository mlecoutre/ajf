package am.ajf.web.controllers;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SecurityBean that enable authentication and allow to display user information
 * on the web interface
 * 
 * @author E010925
 * 
 */
@Named
@SessionScoped
public class SecurityMBean implements Serializable {

	private static final String OUTCOME_OK = "/index";

	private static final long serialVersionUID = 1L;
	private static final String OUTCOME_ACCESS_DENIED = "/ajf/errors/accessDenied";
	private transient Logger log = LoggerFactory.getLogger(SecurityMBean.class);

	private String username;
	private String password;

	/**
	 * Default constructor
	 */
	public SecurityMBean() {
		super();
	}

	/**
	 * Enable authentication
	 * 
	 * @return outcome value to be redirected
	 */
	public String doLogin() {

		log.debug(String.format(" Try to authentify %s", username));
		Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory();
		org.apache.shiro.mgt.SecurityManager securityManager = factory
				.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		UsernamePasswordToken token = new UsernamePasswordToken(username,
				password);
		Subject user = SecurityUtils.getSubject();
		// FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		try {
			user.login(token);
			log.debug(String.format("%s is Authenticated: ", username,
					user.isAuthenticated()));
			return OUTCOME_OK;
		} catch (AuthenticationException ae) {
			log.error(String.format(
					"Impossible to authenticate username %s: %s", username,
					ae.getMessage()));

			return OUTCOME_ACCESS_DENIED;
		}

	}

	/**
	 * Return true if the user is logged in (what ever the role)
	 * 
	 * 
	 * @return boolean value that say that user is authentified ornot.
	 */
	public boolean getIsLogIn() {

		Subject currentSubject = SecurityUtils.getSubject();
		return currentSubject.isAuthenticated();
	}

	/**
	 * user logout and redirect to index.jsf
	 * 
	 * @return index destination
	 */
	public String doLogout() {
		Subject currentSubject = SecurityUtils.getSubject();
		log.debug(String.format("Logout %s", username));
		currentSubject.logout();
		return OUTCOME_OK;
	}

	/**
	 * Use <i>EL 2.1+</i> to know if user is allowed to access to the resource.
	 * 
	 * @param roles
	 *            user roles comma-separated
	 * @return isAllowed boolean
	 */
	public boolean isAllowed(String roles) {

		boolean isAllowed = false;
		Subject currentSubject = SecurityUtils.getSubject();
		if (roles.contains(",")) {
			String[] rolesArray = StringUtils.split(roles, ',');
			List<String> listRoles = Arrays.asList(rolesArray);
			isAllowed = currentSubject.hasAllRoles(listRoles);
		} else {
			isAllowed = currentSubject.hasRole(roles);
		}

		log.debug(String.format("%s for role %s : %s",
				currentSubject.getPrincipal(), roles, isAllowed));
		return isAllowed;
	}

	/**
	 * 
	 * @return viewId of the current page
	 */
	public String getViewId() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		HttpServletRequest servletRequest = (HttpServletRequest) ctx
				.getExternalContext().getRequest();
		// returns something like "/myapplication/home.faces"
		String fullURI = servletRequest.getRequestURI();
		return fullURI;
	}

	/**
	 * 
	 * @return username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 
	 * @param username
	 *            current username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 
	 * @param password
	 *            password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

}
