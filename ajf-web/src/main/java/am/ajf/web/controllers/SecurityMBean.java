package am.ajf.web.controllers;

import java.io.Serializable;
import java.security.Principal;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.security.spi.UserRegistry;
import am.ajf.security.utils.CookieUtils;
import am.ajf.security.utils.UserAccount;

/**
 * SecurityBean that enable authentication and allow to display user information
 * on the web interface
 * 
 * @author E010925
 * 
 */
@Named
@ApplicationScoped
public class SecurityMBean implements Serializable {

	private static final String USER_DISPLAY_NAME_ATTRIBUTE = "am.ajf.UserRegistry.userDisplayNameAttribute";

	private static final String USER_REGISTRY = "am.ajf.UserRegistry";

	//private static final String OUTCOME_OK = "/ajf/login/login.jsf?faces-redirect=true";
	//private static final String OUTCOME_OK = "/ajf/login/logout.html";
	//private static final String OUTCOME_OK = "ajf/login/login.jsf";
	
	private static final long serialVersionUID = 1L;

	private transient Logger log = LoggerFactory.getLogger(SecurityMBean.class);

	// private static final String OUTCOME_ACCESS_DENIED =
	// "/ajf/errors/accessDenied";
	
	/**
	 * Default constructor
	 */
	public SecurityMBean() {
		super();
	}

	/**
	 * Return true if the user is logged in (what ever the role)
	 * 
	 * 
	 * @return boolean value that say that user is authentified ornot.
	 */
	public boolean getIsLogIn() {

		/*
		 * Subject currentSubject = SecurityUtils.getSubject(); return
		 * currentSubject.isAuthenticated();
		 */

		FacesContext ctx = FacesContext.getCurrentInstance();
		HttpServletRequest servletRequest = (HttpServletRequest) ctx
				.getExternalContext().getRequest();
		return (null != servletRequest.getUserPrincipal());

	}

	/**
	 * user logout and redirect to index.jsf
	 * 
	 * @return index destination
	 */
	public void doLogout() {

		// Subject currentSubject = SecurityUtils.getSubject();
		String username = getUsername();
		if (null == username)
			return;
		
		log.debug(String.format("Logout %s", username));

		FacesContext ctx = FacesContext.getCurrentInstance();

		// remove cookies if required
		HttpServletRequest req = (HttpServletRequest) ctx.getExternalContext()
				.getRequest();
		HttpServletResponse resp = (HttpServletResponse) ctx
				.getExternalContext().getResponse();

		removeCookie(req, username, CookieUtils.AJF_COOKIE_NAME, resp);
		removeCookie(req, username, CookieUtils.MEOW_COOKIE_NAME, resp);
		
		HttpSession userSession = (HttpSession) ctx.getExternalContext()
				.getSession(false);
		if (null != userSession) {
			userSession.invalidate();
		}

		// currentSubject.logout();
		//return OUTCOME_OK;

	}

	protected void removeCookie(HttpServletRequest req, String username, String cookieName,
			HttpServletResponse resp) {
		
		Cookie cookie = CookieUtils.retieveCookie(cookieName, req);
		if (null != cookie) {
			UserAccount userAccount = CookieUtils.decodeCookie(cookie);
			if (null != userAccount) {
				if (username.equals(userAccount.getUser())) {
					CookieUtils.removeCookie(cookie, req);
					resp.addCookie(cookie);
				}
			}
		}
	}

	/**
	 * 
	 * @return the User Principal
	 */
	public Principal getPrincipal() {

		FacesContext ctx = FacesContext.getCurrentInstance();
		HttpServletRequest servletRequest = (HttpServletRequest) ctx
				.getExternalContext().getRequest();
		Principal principal = servletRequest.getUserPrincipal();

		return principal;

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

		FacesContext ctx = FacesContext.getCurrentInstance();
		HttpServletRequest servletRequest = (HttpServletRequest) ctx
				.getExternalContext().getRequest();
		Principal principal = servletRequest.getUserPrincipal();

		if (roles.contains(",")) {
			String[] rolesArray = roles.split("[ ]*,[ ]*");
			for (String role : rolesArray) {
				if ((null != role) && (0 != role.trim().length())) {
					isAllowed = servletRequest.isUserInRole(role);
					if (!isAllowed) {
						break;
					}
				}
			}
		} else {
			isAllowed = servletRequest.isUserInRole(roles);
		}

		log.debug(String.format("%s for role %s : %s", principal, roles,
				isAllowed));
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
		Principal principal = getPrincipal();
		if (null != principal)
			return principal.getName();
		return null;
	}

	/**
	 * 
	 * @return displayname
	 */
	public String getDisplayname() {

		String displayname = null;

		Principal principal = getPrincipal();
		if (null != principal) {
			String userId = principal.getName();
			displayname = userId;
			try {
				ServletContext servletCtx = (ServletContext) FacesContext
						.getCurrentInstance().getExternalContext().getContext();
				String userRegistryName = servletCtx
						.getInitParameter(USER_REGISTRY);
				String userDisplayNameAttribute = servletCtx
						.getInitParameter(USER_DISPLAY_NAME_ATTRIBUTE);

				Context context = new InitialContext();

				UserRegistry userRegistry = (UserRegistry) context
						.lookup(userRegistryName);

				// try to resolve the user unique id
				String userUniqueId = userRegistry.getUserUniqueId(userId);

				// read th displayName attribute
				Object[] values = userRegistry.readAttribute(userUniqueId,
						userDisplayNameAttribute);
				if ((null != values) && (values.length > 0)) {
					displayname = (String) values[0];
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

		}

		return displayname;
	}
	
}
