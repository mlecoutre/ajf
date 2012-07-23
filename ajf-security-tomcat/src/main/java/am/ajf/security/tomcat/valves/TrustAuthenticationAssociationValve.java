package am.ajf.security.tomcat.valves;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import am.ajf.security.utils.CookieUtils;
import am.ajf.security.utils.PrincipalUtils;
import am.ajf.security.utils.UserAccount;

public class TrustAuthenticationAssociationValve extends ValveBase {

	// SUN Access Manager Section

	public final static String SUNAM_HEADER_FIELD_NAME = "SUNAM_HEADER_FIELD";
	public final static String SUNAM_DEFAULT_HEADER_FIELD = "HTTP_LOGON_USER";

	private static final String FORM_AUTHENTICATION_ACTION = "/j_security_check";
	private static final String FORM_AUTHENTICATION_PASSWORD = "j_password";

	private static final String USER_PRIVATE_CREDENTIAL_SESSION_ATTRIBUTE = TrustAuthenticationAssociationValve.class
			.getName().concat("#UserPrivateCredential");

	private String sunAMHeaderField = TrustAuthenticationAssociationValve.SUNAM_DEFAULT_HEADER_FIELD;

	private boolean createCookie = false;
	
	private String cookieDomain = null;
	private boolean cookiePath = true;
	private int cookieExpiry = 0;
	
	protected boolean enforceCookieAuthentication = true;
	
	public TrustAuthenticationAssociationValve() {
		super();
	}

	public TrustAuthenticationAssociationValve(boolean asyncSupported) {
		super(asyncSupported);
	}

	public String getSunAMHeaderField() {
		if ((null == sunAMHeaderField)
				|| (0 == SUNAM_HEADER_FIELD_NAME.trim().length()))
			sunAMHeaderField = SUNAM_DEFAULT_HEADER_FIELD;
		return sunAMHeaderField;
	}

	public boolean isEnforceCookieAuthentication() {
		return enforceCookieAuthentication;
	}

	public void setEnforceCookieAuthentication(boolean enforceCookieAuthentication) {
		this.enforceCookieAuthentication = enforceCookieAuthentication;
	}
	
	public void setSunAMHeaderField(String sunAMHeaderField) {
		this.sunAMHeaderField = sunAMHeaderField;
	}

	public boolean isCreateCookie() {
		return createCookie;
	}

	public void setCreateCookie(boolean createCookie) {
		this.createCookie = createCookie;
	}

	public String getCookieDomain() {
		return cookieDomain;
	}

	public void setCookieDomain(String cookieDomain) {
		this.cookieDomain = cookieDomain;
	}

	public boolean isCookiePath() {
		return cookiePath;
	}

	public void setCookiePath(boolean cookiePath) {
		this.cookiePath = cookiePath;
	}

	public int getCookieExpiry() {
		return cookieExpiry;
	}

	public void setCookieExpiry(int cookieExpiry) {
		this.cookieExpiry = cookieExpiry;
	}
	
	@Override
	public void invoke(Request request, Response response) throws IOException,
			ServletException {

		Principal principal = request.getPrincipal();
		if (principal != null) {
			// continue Valves chain
			getNext().invoke(request, response);
		} else {
			
			String requestedURI = request.getRequestURI();
			boolean targetSecurityCheck = requestedURI
					.endsWith(FORM_AUTHENTICATION_ACTION);

			if (targetSecurityCheck) {
				
				if (createCookie) {

					// capture the user credential
					String userPrivateCredential = request
							.getParameter(FORM_AUTHENTICATION_PASSWORD);
	
					HttpSession userSession = request.getSession(true);
					userSession.setAttribute(
							USER_PRIVATE_CREDENTIAL_SESSION_ATTRIBUTE,
							userPrivateCredential);
					containerLog.debug("Store the user private credential.");
				
				}
				
				// User Authentication Not in trusted mode
				PrincipalUtils.hasToTrustUser(false);

				// continue Valves chain
				getNext().invoke(request, response);
				
			} else {

				principal = request.getPrincipal();
				boolean alreadyAuthenticated = (null != principal);

				// try cookies authentication if not already authenticated
				if (!alreadyAuthenticated) {

					String userId = retrieveSunAMIdentifier(request);
					if ((null != userId) && (userId.length() > 0)) {
						principal = trustUser(userId);
					} else {
						principal = authenticateFromCookie(request,
								CookieUtils.AJF_COOKIE_NAME);
						if (null == principal) {
							principal = authenticateFromCookie(request,
									CookieUtils.MEOW_COOKIE_NAME);
						}
					}
					// authenticated, so set the UserPrincipal
					if (null != principal) {
						request.setUserPrincipal(principal);
					}
				}

				// continue Valves chain
				getNext().invoke(request, response);

				principal = request.getPrincipal();
				if (!targetSecurityCheck && (null != principal)
						&& (!alreadyAuthenticated)) {

					try {
						HttpSession userSession = request.getSession();
						if ((null != userSession) && (createCookie)) {
						
							String userPrivateCredential = (String) userSession
									.getAttribute(USER_PRIVATE_CREDENTIAL_SESSION_ATTRIBUTE);
							if (null != userPrivateCredential) {
								createCookie(request, response, principal,
										userPrivateCredential,
										CookieUtils.AJF_COOKIE_NAME);
								createCookie(request, response, principal,
										userPrivateCredential,
										CookieUtils.MEOW_COOKIE_NAME);

								userSession
										.removeAttribute(USER_PRIVATE_CREDENTIAL_SESSION_ATTRIBUTE);
							}
						}
					} catch (Exception e) {
						// Nothing to do
					}

				}

			}

		}

	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	protected Principal trustUser(String userId)  {

		// User Authentication in Trusted mode
		PrincipalUtils.hasToTrustUser(true);
		
		Principal principal = this
				.getContainer()
				.getRealm()
				.authenticate(userId,
						null);
		return principal;
		
	}

	/**
	 * 
	 * @param req
	 * @return
	 */
	protected String retrieveSunAMIdentifier(Request req) {
		String userid = null;
		// Sun AM invocation section
		userid = req.getHeader(getSunAMHeaderField());
		return userid;
	}

	/**
	 * 
	 * @param request
	 * @param cookieName
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	protected Principal authenticateFromCookie(Request request,
			String cookieName) throws ServletException, IOException {

		Principal principal = null;

		Cookie cookie = CookieUtils.retieveCookie(cookieName,
				request.getRequest());
		if (null != cookie) {
			UserAccount userAccount = CookieUtils.decodeCookie(cookie);
			if (null != userAccount) {

				if (enforceCookieAuthentication) {
					// User Authentication NOT in trusted mode
					PrincipalUtils.hasToTrustUser(false);
					
					principal = this
							.getContainer()
							.getRealm()
							.authenticate(userAccount.getUser(),
									new String(userAccount.getPassword()));
				} else {
					principal = trustUser(userAccount.getUser());
				}

				if (null != principal) {
					containerLog.debug("Authenticated from Cookie "
							.concat(cookieName));
				}
			}

		}
		return principal;

	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param principal
	 * @param userPrivateCredential
	 * @param cookieName
	 */
	protected void createCookie(Request request, Response response,
			Principal principal, String userPrivateCredential, String cookieName) {

		try {
			Cookie cookie = CookieUtils.createCookie(
					cookieName,
					new UserAccount(principal.getName(), userPrivateCredential
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
			
			containerLog.info(String.format("Create cookie '%s' for user: %s.", cookieName, principal.getName()));
		} catch (Throwable e) {
			containerLog.error(
					"Unable to install Cookie '" + cookieName + "'.", e);
		}

	}

	@Override
	protected void initInternal() throws LifecycleException {
		
		super.initInternal();
						
	}
	
	

}
