package am.ajf.web.servlets;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.security.utils.CookieUtils;
import am.ajf.security.utils.UserAccount;

public class LogoutServlet extends HttpServlet {

	public static final String LOGOUT_FORWARD_ATTRIBUTE = "am.ajf.security.logout.redirect";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private transient Logger log = LoggerFactory.getLogger(LogoutServlet.class);
	
	private static final String AJF_LOGIN_PAGE = "/ajf/login/login.jsf";
	private static String redirectPath = AJF_LOGIN_PAGE;
		

	public LogoutServlet() {
		super();
	}
	

	@Override
	public void init(ServletConfig config) throws ServletException {
		log.info("LogoutServlet initialized.");
		
		try {
			redirectPath = config.getInitParameter(LOGOUT_FORWARD_ATTRIBUTE);
		} catch (Exception e) {
			redirectPath = null;
		}
		
		if ((null == redirectPath) || (0 == redirectPath.trim().length())) {
			redirectPath = AJF_LOGIN_PAGE;
		}
		
	}	
	
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		HttpSession userSession = req.getSession();
		if (null != userSession) {			String userName = req.getRemoteUser();
			if (null != userName) {
				log.debug(String.format("Logout %s", userName));
				// remove cookies if required
				if (null != userName) {
					removeCookie(req, CookieUtils.AJF_COOKIE_NAME, userName, resp);
					removeCookie(req, CookieUtils.MEOW_COOKIE_NAME, userName, resp);
				}
				String userSessionId = userSession.getId();
				log.info(String.format("Want to invalidate session #%s", userSessionId));
				userSession.invalidate();
				
			}
		}

		// redirect the user
		//String redirectURI = req.getContextPath() + "/index.jsf";
		//resp.sendRedirect(redirectURI);
		//req.getRequestDispatcher(forwardPath).forward(req, resp);

		resp.sendRedirect(redirectPath);
					
	}

	protected void removeCookie(HttpServletRequest req, String cookieName,
			String userName, HttpServletResponse resp) {
		Cookie cookie = CookieUtils.retieveCookie(cookieName, req);
		if (null != cookie) {
			UserAccount userAccount = CookieUtils.decodeCookie(cookie);
			if (null != userAccount) {
				if (userName.equals(userAccount.getUser())) {
					CookieUtils.removeCookie(cookie, req);
					resp.addCookie(cookie);
					log.debug("Remove cookie '" + cookieName + "'");
				}
			}
		}
	}

}
