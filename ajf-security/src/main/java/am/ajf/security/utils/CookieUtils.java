package am.ajf.security.utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author u002617
 */
public abstract class CookieUtils {

	//	cookie management
	public final static String AJF_COOKIE_NAME = "AJFCookie";
	public final static String MEOW_COOKIE_NAME = "MeowCookie";
	
	private final static Logger logger = Logger.getLogger(CookieUtils.class.getName());

	/**
	 * @param cookieName
	 * @param req
	 * @return
	 */
	public static Cookie retieveCookie(
		String cookieName,
		HttpServletRequest req) {

		logger.log(Level.FINE, "Search for cookie " + cookieName);

		Cookie result = null;
		Cookie[] listCookies = req.getCookies();
		if (listCookies != null) {
			int nbCookies = listCookies.length;
			for (int i = 0; i < nbCookies; i++) {
				Cookie c = listCookies[i];
				String name = c.getName();
				if (name.equals(cookieName)) {
					logger.log(Level.FINE, "Find cookie " + cookieName);
					result = c;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * @param the Cookie Name
	 * @param UserAccount
	 * @return the new Cookie
	 */
	public static Cookie createCookie(String cookieName, UserAccount account) {

		if (null == account)
			return null;

		// cryptage du couple (user,password)
		SignatureCrypt sc = new SignatureCrypt();
		sc.setUser(account.getUser());
		sc.setPassword(new String(account.getPassword()));
		sc.crypt();

		Cookie cookie = new Cookie(cookieName, sc.getCryptedString());

		return cookie;

	}

	/**
	 * @param cookie
	 * @return a UserAccount
	 */
	public static UserAccount decodeCookie(Cookie cookie) {

		if (null == cookie)
			return null;

		//		R�cup�ration des informations crypt�es
		String cryptedValue = cookie.getValue();
		try {
			// decrypt the cookie values
			SignatureDecrypt sd = new SignatureDecrypt();
			sd.setCryptedString(cryptedValue);
			sd.decrypt();

			// obtain the userId and password
			String domain = sd.getDomain(); 
			String username = sd.getUser();
			char[] password = sd.getPassword().toCharArray();

			logger.log(Level.FINE, 
				"Retrieve username "
					+ username
					+ " from cookie "
					+ cookie.getName());

			return new UserAccount(domain, username, password);

		} catch (IOException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			return null;
		}

	}

	/**
	 * 
	 * @param cookie
	 * @param req
	 */
	public static void removeCookie(Cookie cookie, HttpServletRequest req) {
		cookie.setMaxAge(0);	
		logger.log(Level.FINE, "Set the maxAge to '0' for the Cookie '" + cookie.getName() + "'");
	}

	
}
