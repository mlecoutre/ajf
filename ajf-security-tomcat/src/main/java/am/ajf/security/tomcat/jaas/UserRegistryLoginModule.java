package am.ajf.security.tomcat.jaas;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.CredentialException;
import javax.security.auth.login.LoginException;

import am.ajf.security.PasswordCredential;
import am.ajf.security.RoleMapping;
import am.ajf.security.RolePrincipal;
import am.ajf.security.UnsupportedAuthenticationTokenException;
import am.ajf.security.UserPrincipal;
import am.ajf.security.spi.UserRegistry;
import am.ajf.security.spi.UserRoleResolver;

public class UserRegistryLoginModule extends AbstractSimpleLoginModule {
	
	private static final String USER_REGISTRY_RESOURCE = "userRegistryResource";
	private static final String USER_ROLES_RESOLVER_RESOURCE = "userRoleResolverResource";

	private final Logger logger = Logger
			.getLogger(UserRegistryLoginModule.class.getName());

	protected String userRegistryResource = null;
	protected String userRoleResolverResource = null;
	
	public UserRegistryLoginModule() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see am.ajf.security.tomcat.jaas.SimpleLoginModule#doInit()
	 */
	protected void doInit() {

		this.userRoleResolverResource = null;
		if (null != (String) this.options.get(USER_ROLES_RESOLVER_RESOURCE)) {
			this.userRoleResolverResource = (String) this.options
					.get(USER_ROLES_RESOLVER_RESOURCE);
		}

		this.userRegistryResource = null;
		if (null != (String) this.options.get(USER_REGISTRY_RESOURCE)) {
			this.userRegistryResource = (String) this.options
					.get(USER_REGISTRY_RESOURCE);
		}
		
	}

	@Override
	protected Subject doLogin(AuthenticationToken authenticationToken, boolean hasToTrustUser) throws LoginException {
		
		Subject retSubject = new Subject();
		Set<Principal> userPrincipals = retSubject.getPrincipals();
			
		try {
			// check user
			if (!(authenticationToken instanceof SimpleAuthenticationToken)) {
				String msg = authenticationToken.getClass().getName() + " is not supported by the LoginModule.";
				logger.warning(msg);
				throw new UnsupportedAuthenticationTokenException(msg);
			}
			
			Context ctx = new InitialContext();
			UserRegistry userRegistry = (UserRegistry) ctx
					.lookup(userRegistryResource);

			UserRoleResolver userRoleResolver = null;
			if (null != userRoleResolverResource) {
				userRoleResolver = (UserRoleResolver) ctx
						.lookup(userRoleResolverResource);
			}
			
			SimpleAuthenticationToken token = (SimpleAuthenticationToken) authenticationToken;
			String userName = token.getUserName();
			
			// can throw 
			//	NameNotFoundException if the user can not be found
			//  NamingException if the LDAP Registry connection failed 
			String uniqueUserId = userRegistry.getUserUniqueId(userName);
			
			// check the user password
			if (! hasToTrustUser) {
				logger.log(Level.FINE, String.format("Try to authenticate user '%s'.", userName));
				char[] password = token.getPassword();
				String pwd = new String(password);
				// can throw
				//  AuthenticationException if the password is not Ok
				userRegistry.checkPassword(userName, pwd);
				retSubject.getPrivateCredentials().add(new PasswordCredential(pwd));
				pwd = null;
			}
			else {
				logger.log(Level.FINE, String.format("Trust user '%s'.", userName));
			}			
						
			// create the user principal
			UserPrincipal userPrincipal = new UserPrincipal(userName, hasToTrustUser,
					uniqueUserId);
			userPrincipals.add(userPrincipal);
			
			// find user roles
			resolveUserRoles(userRegistry, userRoleResolver, uniqueUserId,
					userPrincipals);
			
		} catch (LoginException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		} catch (AuthenticationException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw new CredentialException(e.getMessage());
		} catch (NameNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw new AccountNotFoundException(e.getMessage());
		} catch (NamingException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw new LoginException(e.getMessage());
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw new LoginException(e.getMessage());
		}

		return retSubject;
	}

	/**
	 * 
	 * @param userRegistry
	 * @param userRoleResolver
	 * @param uniqueUserId
	 * @param userPrincipals
	 * @throws NamingException
	 * @throws Exception
	 */
	protected void resolveUserRoles(UserRegistry userRegistry,
			UserRoleResolver userRoleResolver, String uniqueUserId,
			Set<Principal> userPrincipals) throws NamingException, Exception {
		
		String[] roles = userRegistry.findGroupsForUserUniqueId(uniqueUserId);
		if ((null != roles) && (!(0 == roles.length))) {

			// has the roles to be mapped
			if (null != userRoleResolver) {
				Set<RoleMapping> rolesMappingSet = userRoleResolver
						.getRolesMapping();
				if ((null != rolesMappingSet)
						&& (!rolesMappingSet.isEmpty())) {
					Collection<String> groups = Arrays.asList(roles);
					Set<String> rolesSet = userRoleResolver
							.resolveUserRoles(groups);
					if ((null != rolesSet) && (!rolesSet.isEmpty())) {
						roles = rolesSet.toArray(new String[0]);
					} else {
						roles = null;
					}
				}
			}

			// copy roles in the principal set
			if (null != roles) {
				for (String roleName : roles) {
					RolePrincipal rolePrincipal = new RolePrincipal(
							roleName);
					userPrincipals.add(rolePrincipal);
				}
			}

		}
	}

}
