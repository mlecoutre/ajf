package am.ajf.security.spi;

import java.util.Collection;
import java.util.Set;

import am.ajf.security.RoleMapping;


public interface UserRoleResolver {
	
	Set<RoleMapping> getRolesMapping() throws Exception;

	Set<String> resolveUserRoles(
			Collection<String> groups) throws Exception ;

}
