package am.ajf.security.tomcat.jaas;

import java.io.IOException;
import java.io.Serializable;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class SimpleAuthenticationToken implements Serializable, AuthenticationToken {

	private static final String USERNAME_PROMPT = "Username";
	private static final String PASSWORD_PROMPT = "Password";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String userName = null;
	protected transient char[] password = null;

	public SimpleAuthenticationToken() {
		super();
	}

	public String getUserName() {
		return userName;
	}

	public char[] getPassword() {
		return password;
	}

	protected Callback[] buildCallback() {
		Callback callbacks[] = new Callback[2];
		
		callbacks[0] = new NameCallback(USERNAME_PROMPT);
		callbacks[1] = new PasswordCallback(PASSWORD_PROMPT, false);
		return callbacks;
	}

	protected void cleanCallbacks(Callback[] callbacks) {
		((PasswordCallback) callbacks[1]).clearPassword();
	}

	/* (non-Javadoc)
	 * @see am.ajf.security.tomcat.jaas.AuthenticationToken#handleAuthenticationDatas(javax.security.auth.callback.CallbackHandler)
	 */
	@Override
	public void handleAuthenticationDatas(CallbackHandler callbackHandler)
			throws IOException, UnsupportedCallbackException {

		Callback[] callbacks = buildCallback();

		callbackHandler.handle(callbacks);
		
		userName = ((NameCallback) callbacks[0]).getName();
		password = ((PasswordCallback) callbacks[1]).getPassword();
		
		cleanCallbacks(callbacks);

	}
	
	private void hide(char[] password) {
		if (password != null) {
			for (int b = 0; b < password.length; b++)
				password[b] = '\0';
		}
	}
	
	@Override
	public void hideAuthenticationDatas() {
		hide(password);
	}

}
