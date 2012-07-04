/**
 * 
 */
package am.ajf.security.was.filters;

import java.io.IOException;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import am.ajf.security.utils.CookieUtils;
import am.ajf.security.utils.UserAccount;

import com.ibm.websphere.security.auth.WSSubject;

/**
 * @author U002617
 * install on /j_security_check
 */
public class LoginFilter implements Filter {

	public static final String FORM_AUTHENTICATION_PASSWORD = "j_password";

	public static final String COOKIE_EXPIRY = "cookieExpiry";
	public static final String COOKIE_PATH = "cookiePath";
	public static final String COOKIE_DOMAIN = "cookieDomain";
	public static final String CREATE_COOKIE = "createCookie";

	private boolean createCookie = false;
	private String cookieDomain = null;
	private boolean cookiePath = true;
	private int cookieExpiry = 0;

	private Logger logger = Logger.getLogger(LoginFilter.class.getName());

	/**
	 * 
	 */
	public LoginFilter() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {

		String str = config.getInitParameter(CREATE_COOKIE);

		this.createCookie = Boolean.parseBoolean(str);

		str = config.getInitParameter(COOKIE_DOMAIN);
		this.cookieDomain = str;

		str = config.getInitParameter(COOKIE_PATH);
		this.cookiePath = Boolean.parseBoolean(str);

		this.cookieExpiry = -1;
		str = config.getInitParameter(COOKIE_EXPIRY);
		try {
			this.cookieExpiry = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			this.cookieExpiry = -1;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		// invoke j_security_check
		chain.doFilter(request, response);

		HttpServletRequest req = (HttpServletRequest) request;
		Principal principal = req.getUserPrincipal();
		boolean isAuthenticated = (null != principal);

		if (isAuthenticated && createCookie) {
			
			String userPrincipal = principal.getName();
			String userPrivateCredential = req
					.getParameter(FORM_AUTHENTICATION_PASSWORD);

			HttpServletResponse resp = (HttpServletResponse) response;			
			createCookie(req, resp, userPrincipal, userPrivateCredential,
					CookieUtils.AJF_COOKIE_NAME);
			createCookie(req, resp, userPrincipal, userPrivateCredential,
					CookieUtils.MEOW_COOKIE_NAME);

		}

	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param principal
	 * @param userPrivateCredential
	 * @param cookieName
	 */
	private void createCookie(HttpServletRequest request,
			HttpServletResponse response, String principal,
			String userPrivateCredential, String cookieName) {

		try {
			logger.log(Level.FINE, "Create Cookie ".concat(cookieName));

			Cookie cookie = CookieUtils.createCookie(
					cookieName,
					new UserAccount(principal, userPrivateCredential
							.toCharArray()));

			cookie.setSecure(false);
			if ((null != cookieDomain) && (cookieDomain.trim().length() > 0)) {
				cookie.setDomain(cookieDomain);
			}
			if (cookiePath) {
				cookie.setPath(request.getContextPath());
			}
			cookie.setMaxAge(cookieExpiry);

			// install the generated cookie
			response.addCookie(cookie);

		} catch (Throwable e) {
			logger.log(Level.WARNING, "Unable to install Cookie '" + cookieName
					+ "'.", e);
		}

	}

}
