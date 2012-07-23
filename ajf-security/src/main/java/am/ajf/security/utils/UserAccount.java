package am.ajf.security.utils;

import java.io.Serializable;

/**
 * @author u002617
*/
public class UserAccount implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String domain = null;
	private String user = null;
	private char[] password = null;

	/**
	 * 
	 */
	public UserAccount() {
		super();
	}

	public UserAccount(String newDomain, String newUser, char[] newPassword) {
		super();
		setDomain(newDomain);
		setUser(newUser);
		setPassword(newPassword);
	}
	
	public UserAccount(String newUser, char[] newPassword) {
		super();
		setUser(newUser);
		setPassword(newPassword);
	}

	/**
	 * @return
	 */
	public char[] getPassword() {
		return password;
	}

	/**
	 * @return
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param cs
	 */
	public void setPassword(char[] cs) {
		password = cs;
	}

	/**
	 * @param string
	 */
	public void setUser(String string) {
		user = string;
	}

	/**
	 * @return
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @param string
	 */
	public void setDomain(String string) {
		domain = string;
	}

}
