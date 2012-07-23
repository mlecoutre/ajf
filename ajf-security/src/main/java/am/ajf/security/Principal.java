package am.ajf.security;

/**
 * strore the remote principal in the request scope as ThreadLocal
 * @author U002617
 *
 */
public class Principal {

	private final static ThreadLocal<java.security.Principal> userPrincipal = new ThreadLocal<java.security.Principal>();

	private Principal() {
		super();
	}

	/**
	 * set the user principal
	 * @param principal
	 */
	public static void setUserPrincipal(java.security.Principal principal) {
		userPrincipal.set(principal);
	}

	/**
	 * get the user principal
	 * @return
	 */
	public static java.security.Principal getUserPrincipal() {
		java.security.Principal principal = userPrincipal.get();
		return principal;
	}
	

}
