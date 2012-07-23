package am.ajf.security.tomcat.realms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.realm.JNDIRealm;

import am.ajf.security.PropertiesGroupsMappingUserRoleResolver;
import am.ajf.security.RoleMapping;

public class LDAPRealm extends JNDIRealm {

	private static final String USER_CN_ATTRIBUTE = "cn";
	// the groups <-> roles mapping file resource
	protected String rolesGroupsMappingPath = null;
	protected Set<RoleMapping> rolesMappingSet = null;
	protected String userCn = USER_CN_ATTRIBUTE;

	protected PropertiesGroupsMappingUserRoleResolver userRoleResolver = new PropertiesGroupsMappingUserRoleResolver();

	public LDAPRealm() {
		super();
	}

	public String getRolesGroupsMappingPath() {
		return rolesGroupsMappingPath;
	}

	public void setRolesGroupsMappingPath(String rolesGroupsMappingPath) {
		this.rolesGroupsMappingPath = rolesGroupsMappingPath;
	}

	public String getUserCn() {
		return userCn;
	}

	public void setUserCn(String userCn) {
		this.userCn = userCn;
	}

	/**
	 * Check credentials by binding to the directory as the user
	 * 
	 * @param context
	 *            The directory context
	 * @param user
	 *            The User to be authenticated
	 * @param credentials
	 *            Authentication credentials
	 * 
	 * @exception NamingException
	 *                if a directory server error occurs
	 */
	protected boolean bindAsUser(DirContext context, User user,
			String credentials) throws NamingException {

		if (credentials == null || user == null)
			return (false);

		String dn = user.getDN();
		if (dn == null)
			return (false);

		// Validate the credentials specified by the user
		if (containerLog.isTraceEnabled()) {
			containerLog
					.trace("  validating credentials by binding as the user");
		}

		userCredentialsAdd(context, dn, credentials);

		// Elicit an LDAP bind operation
		boolean validated = false;
		try {
			if (containerLog.isTraceEnabled()) {
				containerLog.trace("  binding as " + dn);
			}
			context.getAttributes(userCn);
			validated = true;
		} catch (AuthenticationException e) {
			if (containerLog.isTraceEnabled()) {
				containerLog.trace("  bind attempt failed");
			}
		}

		userCredentialsRemove(context);

		return (validated);
	}

	/**
	 * Configure the context to use the provided credentials for authentication.
	 * 
	 * @param context
	 *            DirContext to configure
	 * @param dn
	 *            Distinguished name of user
	 * @param credentials
	 *            Credentials of user
	 */
	private void userCredentialsAdd(DirContext context, String dn,
			String credentials) throws NamingException {
		// Set up security environment to bind as the user
		context.addToEnvironment(Context.SECURITY_PRINCIPAL, dn);
		context.addToEnvironment(Context.SECURITY_CREDENTIALS, credentials);
	}

	private void userCredentialsRemove(DirContext context)
			throws NamingException {
		// Restore the original security environment
		if (connectionName != null) {
			context.addToEnvironment(Context.SECURITY_PRINCIPAL, connectionName);
		} else {
			context.removeFromEnvironment(Context.SECURITY_PRINCIPAL);
		}

		if (connectionPassword != null) {
			context.addToEnvironment(Context.SECURITY_CREDENTIALS,
					connectionPassword);
		} else {
			context.removeFromEnvironment(Context.SECURITY_CREDENTIALS);
		}
	}

	protected List<String> getRoles(DirContext context, User user)
			throws NamingException {
		// close context
		close(context);
		// open a new directory context.
		context = open();
		List<String> roles = super.getRoles(context, user);

		// map groups to corresponding roles
		if (null != rolesMappingSet) {
			try {
				Set<String> rolesSet = userRoleResolver.resolveUserRoles(roles);
				roles = new ArrayList<String>();
				if ((null != rolesSet) && (!rolesSet.isEmpty())) {
					for (String role : rolesSet) {
						roles.add(role);
					}
				}
			} catch (Exception e) {
				// Nothing to do
			}

		}

		return roles;
	}

	@Override
	protected void startInternal() throws LifecycleException {

		if (null != getRolesGroupsMappingPath()) {
			try {
				userRoleResolver
						.setRolesGroupsMappingPath(getRolesGroupsMappingPath());
				rolesMappingSet = userRoleResolver.getRolesMapping();
			} catch (Exception e) {
				rolesMappingSet = null;
				e.printStackTrace();
			}
		}
		super.startInternal();
	}

}
