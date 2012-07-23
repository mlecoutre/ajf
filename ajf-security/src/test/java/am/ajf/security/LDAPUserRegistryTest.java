package am.ajf.security;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.junit.BeforeClass;
import org.junit.Test;

import am.ajf.security.spi.UserRegistry;

public class LDAPUserRegistryTest {

	private static UserRegistry userRegistry = null;

	@BeforeClass
	public static void setUpClass() {

		LDAPUserRegistry ldapUserRegistry = new LDAPUserRegistry();
			
		ldapUserRegistry.setRealm("ARMONY");
		ldapUserRegistry.setProviderURL("ldap://APP_LDAPWASHD_PROD:389");
		ldapUserRegistry.setAuthentication("simple");
		ldapUserRegistry.setBindDN("was-reader@armony.net");
		ldapUserRegistry.setBindPWD("re@dPwd0");
		
		ldapUserRegistry.setUserBase("OU=Accounts,dc=armony,dc=net");
		ldapUserRegistry.setUserSearchControlScope("SUBTREE_SCOPE");
		ldapUserRegistry.setUserFilterAttribute("sAMAccountName");
		ldapUserRegistry.setUserDNAttribute("distinguishedname");

		// Dynamic user groups resolving
		ldapUserRegistry.setUserMemberOfAttribute("memberOf");
		
		// Static user groups resolving
		ldapUserRegistry.setGroupBase("OU=Groups,dc=armony,dc=net");
		ldapUserRegistry.setGroupSearchControlScope("SUBTREE_SCOPE");
		ldapUserRegistry.setGroupMembersAttribute("member");
		ldapUserRegistry.setGroupDNAttribute("distinguishedname");
		// Group name attribute
		ldapUserRegistry.setGroupNameAttribute("cn");
		
		userRegistry = ldapUserRegistry;

	}
	
	@Test
	public void testAuthentication() throws Exception {
				
		String user = "sys-maven";
		String password = "M@vejava";
		
		String res = userRegistry.checkPassword(user, password);
		System.out.println(res);
				
	}
	
	@Test
	public void testGetUserGroups() throws NameNotFoundException, Exception {
		
		String userUniqueId = userRegistry.getUserUniqueId("u002617");
		String[] groups = userRegistry.findGroupsForUserUniqueId(userUniqueId);
		for (String group : groups) {
			System.out.println(group);
		}
		
	}
	
	@Test
	public void testGetUser() throws NameNotFoundException, Exception {
		
		String userUniqueId = userRegistry.getUserUniqueId("e019983");
		Map<String, Object[]> attrsMap = userRegistry.readAttributes(userUniqueId);
		
		Set<String> keySet = attrsMap.keySet();
		for (String key : keySet) {
			Object[] values = attrsMap.get(key);
			String value = Arrays.toString(values);
			System.out.println(key + " = " + value);
		}
		
		System.out.println(attrsMap);
		
	}
	
	@Test
	public void testGetGroupsByName() throws NamingException {
		
		//DUN-GG-WEB-DEPLOYCENTER-W
		String[] groups = userRegistry.findGroupsByName("dun-gg-web-deploy");
		for (String group : groups) {
			System.out.println(group);			
		}
				
	}
	
	@Test
	public void testFindUsersForGroupeUniqueId() throws NamingException {
		
		String groupeUniqueId = "CN=DUN-GG-WEB-DEPLOYCENTER-W,OU=Users,OU=Groups,DC=armony,DC=net";
		String[] members = userRegistry.findUsersForGroupeUniqueId(groupeUniqueId);
		
		for (String member : members) {
			System.out.println(member);
		}
		
		
	}

	

	
}
