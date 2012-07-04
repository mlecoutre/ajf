package am.ajf.security.was.tai;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.security.auth.Subject;

import am.ajf.security.utils.CookieUtils;
import am.ajf.security.utils.UserAccount;

import com.ibm.websphere.security.CustomRegistryException;
import com.ibm.websphere.security.EntryNotFoundException;
import com.ibm.websphere.security.UserRegistry;
import com.ibm.websphere.security.WebTrustAssociationException;
import com.ibm.websphere.security.WebTrustAssociationFailedException;

import com.ibm.wsspi.security.tai.TAIResult;
import com.ibm.wsspi.security.tai.TrustAssociationInterceptor;
import com.ibm.wsspi.security.token.AttributeNameConstants;

public class AMTrustAssociationInterceptor implements TrustAssociationInterceptor {

	// SUN Access Manager Section
	
	private static final String ALLOW_BACK_DOOR = "allowBackDoor";
	private static final String ENFORCE_COOKIE_AUTHENTICATION = "enforceCookieAuthentication";
	public final static String SUNAM_HEADER_FIELD_NAME = "SUNAM_HEADER_FIELD";
	public final static String SUNAM_DEFAULT_HEADER_FIELD = "HTTP_LOGON_USER";
	
	private String sunAMHeaderField = AMTrustAssociationInterceptor.SUNAM_DEFAULT_HEADER_FIELD;
	private boolean enforceCookieAuthentication;
	private boolean allowBackDoor;

	// Back door
	private final static String BACK_DOOR_PARAMETER = "USERID";
	
	// OpenSecu (AJF & MEOW) section
		
	public AMTrustAssociationInterceptor() {
		super();
	}
	
	public String getSunAMHeaderField() {
		if ((null == sunAMHeaderField) || (0 == SUNAM_HEADER_FIELD_NAME.trim().length()))
				sunAMHeaderField = SUNAM_DEFAULT_HEADER_FIELD;
		return sunAMHeaderField;
	}
	
	public boolean isEnforceCookieAuthentication() {
		return enforceCookieAuthentication;
	}

	public boolean isAllowBackDoor() {
		return allowBackDoor;
	}

	public void cleanup() {
		//Nothing to do
	}

	public String getType() {
		return this.getClass().getName();
	}

	public String getVersion() {
		return "1.0";
	}

	public int initialize(Properties props) throws WebTrustAssociationFailedException {
		
		String str = props.getProperty(SUNAM_HEADER_FIELD_NAME);
		sunAMHeaderField = str;
		
		String strEnforceCookieAuthentication = props.getProperty(ENFORCE_COOKIE_AUTHENTICATION);
		
		try {
			enforceCookieAuthentication = Boolean.valueOf(strEnforceCookieAuthentication);
		} catch (Exception e) {
			enforceCookieAuthentication = false;
		}

		try {
			allowBackDoor = Boolean.valueOf(ALLOW_BACK_DOOR);
		} catch (Exception e) {
			allowBackDoor = false;
		}
		
		
		return 0;
	}

	public boolean isTargetInterceptor(HttpServletRequest req) throws WebTrustAssociationException {

		String userid = retrieveSunAMIdentifier(req);
		if ((null != userid) && (userid.length() > 0)) {
			return true;
		}
		
		// OpenSecu (AJF & MEOW) test invocation section
		UserAccount userAccount = retrieveUserAccount(req, CookieUtils.AJF_COOKIE_NAME);
		if ((null != userAccount) && (null != userAccount.getUser())) { 
			return true;
		}
		userAccount = retrieveUserAccount(req, CookieUtils.MEOW_COOKIE_NAME);
		if ((null != userAccount) && (null != userAccount.getUser())) { 
			return true;
		}
		
		// Back Door section - for test only
		if (isAllowBackDoor()) {
			userid = retrieveBackDoorIdentifier(req);
			if ((null != userid) && (userid.length() > 0)) {
				return true;
			}
		}
		
		return false;
	}

	private String retrieveBackDoorIdentifier(HttpServletRequest req) {
		String userid;
		userid = req.getParameter(BACK_DOOR_PARAMETER);
		return userid;
	}

	private UserAccount retrieveUserAccount(HttpServletRequest req, String cookieName) {
		Cookie cookie = CookieUtils.retieveCookie(cookieName, req);
		if (null == cookie)
			return null;
		UserAccount userAccount = CookieUtils.decodeCookie(cookie);
		return userAccount;
	}

	private String retrieveSunAMIdentifier(HttpServletRequest req) {
		String userid = null;
		
		// Sun AM test invocation section
		userid = req.getHeader(getSunAMHeaderField());
		return userid;
	}

	@SuppressWarnings("rawtypes")
	public TAIResult negotiateValidateandEstablishTrust(HttpServletRequest req, HttpServletResponse resp) throws WebTrustAssociationFailedException {

		try {

			Context ctx = new InitialContext();
			UserRegistry reg = (UserRegistry) ctx.lookup("UserRegistry");
			
			String userOrigin = null;
			
			String userid = null; 
			String uniqueId = null;	
			
			// Try with SunAM Identication
			userid = retrieveSunAMIdentifier(req);
			if ((null != userid) && (userid.length() > 0)) {
				uniqueId = resolveUniqueUserID(userid, reg);
				userOrigin = "Sun Access Manager";
			}
			
			if (null == uniqueId) {
				// OpenSecu - AJF test invocation section
				UserAccount userAccount = retrieveUserAccount(req, CookieUtils.AJF_COOKIE_NAME);
				if ((null != userAccount) && (null != userAccount.getUser())) { 
					userid = userAccount.getUser();
					uniqueId = resolveUniqueUserID(userid, reg);
					
					if (isEnforceCookieAuthentication()) {
						String checkedUserId = reg.checkPassword(userid, new String(userAccount.getPassword()));
						System.out.println("Using userid " + checkedUserId + " authenticated with success.");
					}
					
					userOrigin = "AJF Cookie";
				}
			}
			
			if (null == uniqueId) {
				// OpenSecu - MEOW test invocation section
				UserAccount userAccount = retrieveUserAccount(req, CookieUtils.MEOW_COOKIE_NAME);
				if ((null != userAccount) && (null != userAccount.getUser())) { 
					userid = userAccount.getUser();
					uniqueId = resolveUniqueUserID(userid, reg);
					
					if (isEnforceCookieAuthentication()) {
						String checkedUserId = reg.checkPassword(userid, new String(userAccount.getPassword()));
						System.out.println("Using userid " + checkedUserId + " authenticated with success.");
					}
					
					userOrigin = "MEOW Cookie";
				}
			}
			
			if (isAllowBackDoor() && (null == uniqueId)) {
				// Back Door test invocation section
				userid = retrieveBackDoorIdentifier(req);
				if ((null != userid) && (userid.length() > 0)) {
					uniqueId = resolveUniqueUserID(userid, reg);
					userOrigin = "Back Door";
				}	
			}
				
			if (null == uniqueId) {
				throw new EntryNotFoundException("Unable to find unique user id for user " + userid + "from [" + userOrigin + "]");
			}
			
			System.out.println("Using userid " + userid + " from [" + userOrigin + "]");
						
			String key = uniqueId;
			List groups = reg.getGroupsForUser(userid);
			Subject subject = createSubject(userid, uniqueId, convertGroupsToUniqueIds(reg, groups), key);
			//Subject subject = createSubject(userid, uniqueId, null, key);
			
			return TAIResult.create(HttpServletResponse.SC_OK, userid, subject);
			
		} catch (Exception e) {
			System.out.println("Exception " + e);
			e.printStackTrace(System.err);
			throw new WebTrustAssociationFailedException(e.getMessage());
		}
		
	}

	private String resolveUniqueUserID(String userId, UserRegistry userRegistry) throws EntryNotFoundException, CustomRegistryException, RemoteException {
		String uniqueId = userRegistry.getUniqueUserId(userId);
		return uniqueId;
	}
	
	@SuppressWarnings("unused")
	private boolean isValidUserID(String userId, UserRegistry userRegistry) throws CustomRegistryException, RemoteException, EntryNotFoundException {
		if (!userRegistry.isValidUser(userId)) {
			throw new EntryNotFoundException("The user '" + userId + "' v=can not be found.");
		}
		return true;		
	}

	@SuppressWarnings("rawtypes")
	private Subject createSubject(String userid, String uniqueId, List groups, String key) {

		Subject subject = new Subject();
		
		Hashtable<String, Object> hashtable = new Hashtable<String, Object>();
		hashtable.put(AttributeNameConstants.WSCREDENTIAL_UNIQUEID, uniqueId);
		hashtable.put(AttributeNameConstants.WSCREDENTIAL_SECURITYNAME, userid);
		if (null != groups)
			hashtable.put(AttributeNameConstants.WSCREDENTIAL_GROUPS, groups);
		
		hashtable.put(AttributeNameConstants.WSCREDENTIAL_CACHE_KEY, key);
		
		subject.getPublicCredentials().add(hashtable);
		
		//subject.getPublicCredentials().add(whateverYouWant);
		
		return subject;
	}

	@SuppressWarnings("rawtypes")
	private List convertGroupsToUniqueIds(UserRegistry reg, List groups) throws EntryNotFoundException, CustomRegistryException, RemoteException {

		if ((null == groups) || (groups.isEmpty()))
			return null;
		
		Iterator iter = groups.iterator();
		List<String> result = new ArrayList<String>();
		
		while (iter.hasNext()) {
			String groupeId = reg.getUniqueGroupId((String) iter.next());
			result.add(groupeId);
		}
		
		return result;
	}

}
