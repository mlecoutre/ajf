package am.ajf.web.controllers;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
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

@ManagedBean
@SessionScoped
public class SecurityBean implements Serializable {

	private static final transient Logger log = LoggerFactory
			.getLogger(SecurityBean.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String username;
	private String password;

	public SecurityBean() {
		super();
	}

	/**
	 * Enable authentication
	 */
	public String doLogin() {

		log.debug("doLogin " + username);
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
			log.debug("is Authenticated: " + user.isAuthenticated());
			return "/index.xhtml";
		} catch (AuthenticationException ae) {
			log.error(String.format(
					"Impossible to authenticate username %s: %s", username,
					ae.getMessage()));
			return "/ajf/errors/accessDenied.xhtml";
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
		log.debug("logout");
		currentSubject.logout();
		return "/index.jsf";
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
	
	public String getViewId(){
		FacesContext ctx = FacesContext.getCurrentInstance();
		HttpServletRequest servletRequest = (HttpServletRequest) ctx.getExternalContext().getRequest();
		// returns something like "/myapplication/home.faces"
		String fullURI = servletRequest.getRequestURI();
		return fullURI;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
