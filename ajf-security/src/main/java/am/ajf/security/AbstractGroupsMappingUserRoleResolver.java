package am.ajf.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import am.ajf.security.spi.UserRoleResolver;


public abstract class AbstractGroupsMappingUserRoleResolver implements UserRoleResolver {

	private Object synchroToken = new Object();
	protected Set<RoleMapping> rolesMapping = null;
	
	public Set<RoleMapping> getRolesMapping() throws Exception {
		if (null != rolesMapping) {
			return rolesMapping;
		}
		synchronized (synchroToken) {
			if (null != rolesMapping) {
				return rolesMapping;
			}
			rolesMapping = loadRolesMapping();
		}
		return rolesMapping;
			
	}
	
	protected abstract Set<RoleMapping> loadRolesMapping() throws Exception;

	@Override
	public Set<String> resolveUserRoles(
			Collection<String> groups) throws Exception {
		
		// resolve the user roles
		Set<String> userRoles = new HashSet<String>();
		
		if ((null == rolesMapping) || (rolesMapping.isEmpty())) {
			userRoles.addAll(groups);
			return userRoles;
		}
		
		Collection<RoleMapping> rolesMapping = getRolesMapping();

		for (RoleMapping roleMapping : rolesMapping) {
			if (roleMapping.isEveryone()
					|| roleMapping.isAllAuthenticatedUsers()) {
				userRoles.add(roleMapping.getRoleName());
			}
		}

		if ((null != groups) && (!groups.isEmpty())) {
			for (RoleMapping roleMapping : rolesMapping) {
				for (String group : groups) {
					if (roleMapping.containGroup(group)) {
						userRoles.add(roleMapping.getRoleName());
					}
				}
			}
		}
		return userRoles;

	}

}
