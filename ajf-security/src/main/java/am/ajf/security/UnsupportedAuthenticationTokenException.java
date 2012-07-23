/**
 * 
 */
package am.ajf.security;

import javax.security.auth.login.LoginException;

/**
 * @author U002617
 *
 */
public class UnsupportedAuthenticationTokenException extends LoginException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnsupportedAuthenticationTokenException() {
		super();
	}

	public UnsupportedAuthenticationTokenException(String msg) {
		super(msg);
	}

}
