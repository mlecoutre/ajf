package am.ajf.security.utils;


public class PrincipalUtils {
	
	// thread local informations
	private static ThreadLocal<Boolean> trustUser = new ThreadLocal<Boolean>();
	
	private PrincipalUtils() {
		super();
	}
		
	/**
	 * 
	 * @return
	 */
	public static Boolean hasToTrustUser() {
		return  trustUser.get();
	}

	/**
	 * 
	 * @param hasToTrust
	 */
	public static void hasToTrustUser(Boolean hasToTrust) {
		trustUser.set(hasToTrust);
	}
	
}
