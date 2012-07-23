package am.ajf.security.tomcat.jaas;

import java.io.IOException;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

public interface AuthenticationToken {

	void handleAuthenticationDatas(
			CallbackHandler callbackHandler) throws IOException,
			UnsupportedCallbackException;

	void hideAuthenticationDatas();

}