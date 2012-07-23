package am.ajf.security.spi;

import java.util.Map;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

public interface UserRegistry {
	
	/**
	 * 
	 * @return
	 */
	String getRealm();
	
	/**
	 * check if a uniqueUserId exist in the repository
	 * @param uniqueUserId
	 * @return true if uniqueUserId can be resolved
	 */
	boolean existUniqueId(String uniqueUserId);

	/**
	 * 
	 * @param userSecurityName
	 * @return
	 * @throws NameNotFoundException
	 * @throws Exception
	 */
	String getUserUniqueId(String userSecurityName)
			throws NameNotFoundException, Exception;

	/**
	 * 
	 * @param groupName
	 * @return
	 * @throws NameNotFoundException
	 * @throws Exception
	 */
	String getGroupUniqueId(String groupName)
			throws NameNotFoundException, Exception;

	
	/**
	 * 
	 * @param userSecurityName
	 * @param readablePassword
	 * @throws javax.naming.AuthenticationException 
	 */
	String checkPassword(String userSecurityName,
			String password) throws javax.naming.AuthenticationException;

	/**
	 * 
	 * @param uniqueId
	 * @param attributesNames
	 * @return
	 * @throws NamingException
	 */
	Map<String, Object[]> readAttributes(String uniqueId,
			String[] attributesNames) throws NamingException;

	/**
	 * 
	 * @param uniqueId
	 * @param attributeName
	 * @return
	 * @throws NamingException
	 */
	Object[] readAttribute(String uniqueId, String attributeName)
			throws NamingException;

	/**
	 * 
	 * @param uniqueId
	 * @return
	 * @throws NamingException
	 */
	Map<String, Object[]> readAttributes(String uniqueId)
			throws NamingException;

	/**
	 * 
	 * @param uniqueUserId
	 * @return
	 * @throws NamingException
	 */
	String[] findGroupsForUserUniqueId(String uniqueUserId)
			throws NamingException;
	
	/**
	 * 
	 * @param groupeUniqueId
	 * @return
	 * @throws NamingException
	 */
	String[] findUsersForGroupeUniqueId(String groupeUniqueId)
			throws NamingException;
	
	/**
	 * 
	 * @param namePattern
	 * @return
	 * @throws NamingException
	 */
	String[] findGroupsByName(String namePattern)
			throws NamingException;
	
	
	/*
	Collection<Map<String Object[]>> findUserByCriteria(String key,
			String criteria, String[] attributes) throws NamingException;		
	
	Collection<Map<String Object[]>> findGroupsByCriteria(String key,
			String criteria, String[] attributes) throws NamingException;
	*/
}