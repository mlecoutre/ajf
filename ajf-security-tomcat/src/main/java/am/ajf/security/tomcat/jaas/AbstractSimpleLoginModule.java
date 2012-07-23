package am.ajf.security.tomcat.jaas;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import am.ajf.security.utils.PrincipalUtils;

public abstract class AbstractSimpleLoginModule implements LoginModule {

	private static final String REALM = "realm";
	private static final String DEBUG = "debug";
	
	// default options and other info obtained from configuration
	protected Subject subject = null;
	protected CallbackHandler callbackHandler = null;
	protected Map<String, ?> sharedState = null;
	protected Map<String, ?> options = null;

	protected String realm = null;
	protected boolean debug = false;

	protected Subject pendingSubject;
	protected Subject committedSubject;
	
	protected boolean commitSucceeded;

	public AbstractSimpleLoginModule() {
		super();

		pendingSubject = null;
		committedSubject = null;
		
		commitSucceeded = false;
	}

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {

		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		this.options = options;

		this.realm = this.getClass().getSimpleName();
		if (null != (String) options.get(REALM)) {
			this.realm = (String) options.get(REALM);
		}

		this.debug = false;
		if (null != (String) options.get(DEBUG)) {
			this.debug = "true".equalsIgnoreCase((String) options.get(DEBUG));
		}
		
		doInit();
		

	}

	protected void doInit() {
		// Nothing to do		
	}

	@Override
	public boolean login() throws LoginException {

		SimpleAuthenticationToken authenticationToken = new SimpleAuthenticationToken();
		try {
			if (callbackHandler == null)
				throw new LoginException(
						"Error: no CallbackHandler available to garner authentication information from the user");
			
			try {
				authenticationToken.handleAuthenticationDatas(callbackHandler);
			} catch (IOException ioe) {
				throw new LoginException(ioe.getMessage());
			} catch (UnsupportedCallbackException uce) {
				throw new LoginException(
						String.valueOf(String
								.valueOf((new StringBuffer("Error: "))
										.append(uce.getCallback().toString())
										.append(" not available to garner authentication information from the user"))));
			}
			pendingSubject = null;
			
			// check if the user has to be trusted or authenticated
			boolean hasToTrustUser = PrincipalUtils.hasToTrustUser();
			
			pendingSubject = doLogin(authenticationToken, hasToTrustUser);
		} finally {
			authenticationToken.hideAuthenticationDatas();
		}
		return true;

	}

	protected abstract Subject doLogin(AuthenticationToken authenticationToken, boolean hasToTrustUser) 
		throws LoginException;
	
	@Override
	public boolean commit() throws LoginException {
		/*
		if (pending == null)
			return false;
		principals = new HashSet<Principal>();

		Set<Principal> s = subject.getPrincipals();
		for (Principal pendingPrincipal : pending) {
			putPrincipal(s, pendingPrincipal);
		}
		*/
		
		if (pendingSubject == null)
			return false;
		
		committedSubject = new Subject();
		commitSubject();
		commitSucceeded = true;

		return true;
	}

	protected void commitSubject() {
		
		Set<Principal> s = subject.getPrincipals();
		for (Principal pendingPrincipal : pendingSubject.getPrincipals()) {
			s.add(pendingPrincipal);
			committedSubject.getPrincipals().add(pendingPrincipal);
		}
		
		Set<Object> privCred = subject.getPrivateCredentials();
		for (Object pendingPrivCred : pendingSubject.getPrivateCredentials()) {
			privCred.add(pendingPrivCred);
			committedSubject.getPrivateCredentials().add(pendingPrivCred);
		}
		
		Set<Object> pubCred = subject.getPublicCredentials();
		for (Object pendingPubCred : pendingSubject.getPublicCredentials()) {
			pubCred.add(pendingPubCred);
			committedSubject.getPublicCredentials().add(pendingPubCred);
		}
	}

	@Override
	public boolean abort() throws LoginException {
		if (pendingSubject == null)
			return false;
		if (pendingSubject != null && !commitSucceeded)
			pendingSubject = null;
		else
			logout();

		return true;
	}

	protected void doLogout() {
		// Nothing to do		
	}
	
	@Override
	public boolean logout() throws LoginException {
		pendingSubject.getPrincipals().clear();
		pendingSubject.getPrivateCredentials().clear();
		pendingSubject.getPublicCredentials().clear();
		
		pendingSubject = null;

		commitSucceeded = false;
		
		doLogout();

		Set<Principal> s = subject.getPrincipals();
		for (Principal principal : committedSubject.getPrincipals()) {
			s.remove(principal);
		}
		
		Set<Object> pubCreds = subject.getPublicCredentials();
		for (Object pubCred : committedSubject.getPublicCredentials()) {
			pubCreds.remove(pubCred);
		}
		
		Set<Object> privCreds = subject.getPrivateCredentials();
		for (Object privCred : committedSubject.getPrivateCredentials()) {
			privCreds.remove(privCred);
		}

		committedSubject.getPrincipals().clear();
		committedSubject.getPublicCredentials().clear();
		committedSubject.getPrivateCredentials().clear();
		
		committedSubject = null;

		return true;

	}

	

}
