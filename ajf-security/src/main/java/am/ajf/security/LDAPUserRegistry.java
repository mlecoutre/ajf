package am.ajf.security;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import am.ajf.security.spi.UserRegistry;

public class LDAPUserRegistry implements UserRegistry, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_SEARCHCONTROLS_SCOPE_VALUE = SearchControls.SUBTREE_SCOPE;

	private static final String DEFAULT_PKGS_URL_FACTORY = "com.ibm.jndi";
	private static final String DEFAULT_INITIAL_FACTORY = "com.ibm.jndi.LDAPCtxFactory";

	private static final String PKGS_URL_FACTORY = "com.sun.jndi.ldap";
	private static final String INITIAL_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

	private static final String FOLLOW_REF = "follow";
	private static final String SIMPLE_AUTH_MODE = "simple";

	private static final String USER_OBJECT_CLASS = "person";
	private static final String GROUP_OBJECT_CLASS = "group";

	private final Logger logger = Logger.getLogger(LDAPUserRegistry.class
			.getName());

	private String realm;

	private String providerURL = null;
	private String bindDN = null;
	private String bindPWD = null;

	private String referrals = FOLLOW_REF;
	private String authentication = SIMPLE_AUTH_MODE;

	private String userBase = null;
	private String userFilterAttribute = null;
	private String userDNAttribute = null;
	
	private String userSearchControlScope = null;
	private int userSearchControlScopeValue;
	
	private String userObjectClass = USER_OBJECT_CLASS;

	private String groupSearchControlScope = null;
	private int groupSearchControlScopeValue;

	// Attribute for Roles resolving
	// Dynamic user groups resolving
	private String userMemberOfAttribute = null;

	// Static user groups resolving
	private String groupBase = null;
	private String groupFilterAttribute = null;
	private String groupMembersAttribute = null;
	private String groupDNAttribute = null;
	// Group name attribute
	private String groupNameAttribute = null;

	private String groupObjectClass = GROUP_OBJECT_CLASS;
	
	
	// global filter attributes
	private int maxQueryResultNumber = -1;
	private int maxQueryResultTime = 1500;

	public LDAPUserRegistry() {
		super();
	}

	/** Begin : properties section **/
	
	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public String getReferrals() {
		return referrals;
	}

	public void setReferrals(String referrals) {
		this.referrals = referrals;
	}

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

	public String getProviderURL() {
		return providerURL;
	}

	public void setProviderURL(String providerURL) {
		this.providerURL = providerURL;
	}

	public String getUserBase() {
		return userBase;
	}

	public void setUserBase(String userBase) {
		this.userBase = userBase;
	}

	public String getUserFilterAttribute() {
		return userFilterAttribute;
	}

	public void setUserFilterAttribute(String userFilterAttribute) {
		this.userFilterAttribute = userFilterAttribute;
	}

	public String getUserSearchControlScope() {
		return userSearchControlScope;
	}

	public void setUserSearchControlScope(String searchControlScopeAsString) {
		this.userSearchControlScope = searchControlScopeAsString;
		int scope = giveSearchControlScope(searchControlScopeAsString);
		this.userSearchControlScopeValue = scope;
	}

	public int getUserSearchControlScopeValue() {
		return userSearchControlScopeValue;
	}

	public String getGroupSearchControlScope() {
		return groupSearchControlScope;
	}

	public void setGroupSearchControlScope(String searchControlScopeAsString) {
		this.groupSearchControlScope = searchControlScopeAsString;
		int scope = giveSearchControlScope(searchControlScopeAsString);
		this.groupSearchControlScopeValue = scope;
	}

	public int getGroupSearchControlScopeValue() {
		return groupSearchControlScopeValue;
	}

	public String getBindDN() {
		return bindDN;
	}

	public void setBindDN(String bindDN) {
		this.bindDN = bindDN;
	}

	public String getBindPWD() {
		return bindPWD;
	}

	public void setBindPWD(String bindPWD) {
		this.bindPWD = bindPWD;
	}

	public String getUserMemberOfAttribute() {
		return userMemberOfAttribute;
	}

	public void setUserMemberOfAttribute(String userMemberOfAttribute) {
		this.userMemberOfAttribute = userMemberOfAttribute;
	}

	public String getGroupBase() {
		return groupBase;
	}

	public void setGroupBase(String groupBase) {
		this.groupBase = groupBase;
	}

	public String getGroupFilterAttribute() {
		return groupFilterAttribute;
	}

	public void setGroupFilterAttribute(String groupFilterAttribute) {
		this.groupFilterAttribute = groupFilterAttribute;
	}

	public String getGroupMembersAttribute() {
		return groupMembersAttribute;
	}

	public void setGroupMembersAttribute(String groupMembersAttribute) {
		this.groupMembersAttribute = groupMembersAttribute;
	}

	public String getGroupNameAttribute() {
		return groupNameAttribute;
	}

	public void setGroupNameAttribute(String groupNameAttribute) {
		this.groupNameAttribute = groupNameAttribute;
	}

	public int getMaxQueryResultNumber() {
		return maxQueryResultNumber;
	}

	public void setMaxQueryResultNumber(int maxQueryResultNumber) {
		this.maxQueryResultNumber = maxQueryResultNumber;
	}

	public int getMaxQueryResultTime() {
		return maxQueryResultTime;
	}

	public void setMaxQueryResultTime(int maxQueryResultTime) {
		this.maxQueryResultTime = maxQueryResultTime;
	}

	public String getGroupDNAttribute() {
		return groupDNAttribute;
	}

	public void setGroupDNAttribute(String groupDNAttribute) {
		this.groupDNAttribute = groupDNAttribute;
	}

	public String getUserDNAttribute() {
		return userDNAttribute;
	}

	public void setUserDNAttribute(String userDNAttribute) {
		this.userDNAttribute = userDNAttribute;
	}

	public String getUserObjectClass() {
		return userObjectClass;
	}

	public void setUserObjectClass(String userObjectClass) {
		this.userObjectClass = userObjectClass;
	}

	public String getGroupObjectClass() {
		return groupObjectClass;
	}

	public void setGroupObjectClass(String groupObjectClass) {
		this.groupObjectClass = groupObjectClass;
	}
	
	/** End : properties section **/
	
	/** Begin : internal section **/

	public Hashtable<Object, Object> getEnvir() {

		Hashtable<Object, Object> env = new Hashtable<Object, Object>();

		String initial;
		String pkgs;
		try {
			initial = INITIAL_FACTORY;
			pkgs = PKGS_URL_FACTORY;
			Class.forName(initial);
		} catch (Exception ex) {
			initial = DEFAULT_INITIAL_FACTORY;
			pkgs = DEFAULT_PKGS_URL_FACTORY;
		}
		env.put(Context.INITIAL_CONTEXT_FACTORY, initial);
		env.put(Context.URL_PKG_PREFIXES, pkgs);

		env.put(Context.PROVIDER_URL, getProviderURL());

		return env;

	}

	/**
	 * get Context
	 * 
	 * @param uid
	 *            userId
	 * @param pwd
	 *            password
	 * @return a valid Context
	 */
	public DirContext getContext(String uid, String pwd) throws NamingException {

		DirContext ctx = null;

		Hashtable<Object, Object> envir = getEnvir();

		envir.put(Context.SECURITY_AUTHENTICATION, authentication);
		envir.put(Context.SECURITY_PRINCIPAL, uid);
		envir.put(Context.SECURITY_CREDENTIALS, pwd);

		envir.put(Context.REFERRAL, referrals);

		ctx = new InitialDirContext(envir);

		return ctx;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see am.ajf.security.UserRegistry#existUniqueId(java.lang.String)
	 */
	@Override
	public boolean existUniqueId(String uniqueId) {

		DirContext ctx = null;
		try {
			ctx = getContext(getBindDN(), getBindPWD());
			Object obj = ctx.lookup(uniqueId);
			return (null != obj);
		} catch (Exception exc) {
			return false;
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (Throwable t) {
					// Nothing to do
				}
			}
		}

	}

	/**
	 * Execute doFilter
	 * 
	 * @param base
	 *            base
	 * @param filter
	 *            filter
	 * @param scope
	 *            scope
	 * @return a NamingEnumeration corresponding to the result of the filter
	 * @throws NamingException
	 *             raise NamingException
	 */
	public NamingEnumeration<SearchResult> doFilter(String base, String filter,
			int scope, String[] returnedAttributes) throws NamingException {

		SearchControls constraints = buildSearchControls(scope,
				returnedAttributes);

		DirContext ctx = null;
		try {
			ctx = getContext(getBindDN(), getBindPWD());
			NamingEnumeration<SearchResult> results = ctx.search(base, filter,
					constraints);
			return results;
		} finally {
			if (ctx != null) {
				ctx.close();
			}
		}

	}

	/*
	 * 
	 */
	public SearchControls buildSearchControls(int scope,
			String[] returnedAttributes) {
		SearchControls constraints = new SearchControls();

		constraints.setSearchScope(scope);

		constraints.setDerefLinkFlag(true);
		constraints.setReturningAttributes(new String[0]);

		if (maxQueryResultNumber > 0)
			constraints.setCountLimit(maxQueryResultNumber);
		if (maxQueryResultTime > 0)
			constraints.setTimeLimit(maxQueryResultTime);

		if (null != returnedAttributes) {
			constraints.setReturningObjFlag(true);
			constraints.setReturningAttributes(returnedAttributes);
		}

		return constraints;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see am.ajf.security.UserRegistry#getUniqueUserId(java.lang.String)
	 */
	@Override
	public String getUserUniqueId(String userSecurityName)
			throws NameNotFoundException, Exception {

		String dn = null;

		String base = getUserBase();

		String filter = String.format("(%s=%s)", getUserFilterAttribute(),
				userSecurityName);
		NamingEnumeration<SearchResult> results = doFilter(base, filter,
				getUserSearchControlScopeValue(),
				new String[] { getUserDNAttribute() });

		try {
			if (results.hasMoreElements()) {
				SearchResult sr = results.nextElement();
				String key = sr.getAttributes().getIDs().nextElement();
				dn = (String) sr.getAttributes().get(key).get();
			}

			if (null == dn)
				throw new NameNotFoundException(String.format("Unable to find user '%s'.", userSecurityName));

		} finally {
			results.close();
		}

		return dn;

	}

	/*
	 * (non-Javadoc)
	 * @see am.ajf.security.spi.UserRegistry#getGroupUniqueId(java.lang.String)
	 */
	@Override
	public String getGroupUniqueId(String groupName)
			throws NameNotFoundException, Exception {

		String dn = null;

		String base = getGroupBase();

		String filter = String.format("(%s=%s)", getGroupFilterAttribute(),
				groupName);
		NamingEnumeration<SearchResult> results = doFilter(base, filter,
				getGroupSearchControlScopeValue(),
				new String[] { getGroupDNAttribute()});

		try {
			if (results.hasMoreElements()) {
				SearchResult sr = results.nextElement();
				String key = sr.getAttributes().getIDs().nextElement();
				dn = (String) sr.getAttributes().get(key).get();
			}

			if (null == dn)
				throw new NameNotFoundException();

		} finally {
			results.close();
		}

		return dn;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see am.ajf.security.UserRegistry#checkPassword(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String checkPassword(String userSecurityName, String password)
			throws javax.naming.AuthenticationException {

		Context ctx = null;
		try {
			String uid = getUserUniqueId(userSecurityName);
			ctx = getContext(uid, password);
		} catch (Exception exc) {
			throw new javax.naming.AuthenticationException(
					"Unable to connect to the directory " + getProviderURL()
							+ " with the user " + userSecurityName
							+ ", exception is '" + exc.getClass().getName()
							+ " : " + exc.getMessage() + "'");
		} finally {
			if (null != ctx) {
				try {
					ctx.close();
				} catch (NamingException e1) {
					// Nothing to do
				}
			}
		}

		return userSecurityName;
	}

	/**
	 * 
	 * @param mSearchScopeConfigValue
	 * @return
	 */
	public int giveSearchControlScope(String mSearchScopeConfigValue) {

		Class<?> searchControls = SearchControls.class;
		int scope = DEFAULT_SEARCHCONTROLS_SCOPE_VALUE;
		try {
			if (mSearchScopeConfigValue != null
					&& mSearchScopeConfigValue.length() > 0) {
				Field field = searchControls.getField(mSearchScopeConfigValue);
				scope = field.getInt(null);
			} else {
				scope = DEFAULT_SEARCHCONTROLS_SCOPE_VALUE;
			}

		} catch (Exception exception) {
			scope = DEFAULT_SEARCHCONTROLS_SCOPE_VALUE;
		}
		return scope;
	}

	public Hashtable<String, Object[]> extractAttributes(Attributes attrs)
			throws NamingException {

		Hashtable<String, Object[]> res = new Hashtable<String, Object[]>();
		NamingEnumeration<?> e = null;

		try {

			for (e = attrs.getAll(); e.hasMoreElements();) {
				Attribute attr = (Attribute) e.nextElement();
				String key = attr.getID();

				// if multi-Valuated
				Object[] values = new Object[attr.size()];
				for (int i = 0; i < attr.size(); i++) {
					Object attrValue = attr.get(i);
					values[i] = attrValue;
				}

				res.put(key, values);

			}

		} finally {
			try {
				e.close();
			} catch (Throwable tr) {
			}
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see am.ajf.security.UserRegistry#readAttributes(java.lang.String,
	 * java.lang.String[])
	 */
	@Override
	public Map<String, Object[]> readAttributes(String uniqueId,
			String[] attributesNames) throws NamingException {

		DirContext ctx = null;
		try {
			ctx = getContext(getBindDN(), getBindPWD());
			Attributes attrs = ctx.getAttributes(uniqueId, attributesNames);
			Map<String, Object[]> map = extractAttributes(attrs);
			return map;
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (Throwable t) {
					// Nothing to do
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see am.ajf.security.UserRegistry#readAttribute(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Object[] readAttribute(String uniqueId, String attributeName)
			throws NamingException {

		Map<String, Object[]> map = readAttributes(uniqueId,
				new String[] { attributeName });
		if ((null == map) || (map.isEmpty()))
			return null;

		String key = map.keySet().iterator().next();
		Object[] res = map.get(key);

		return res;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see am.ajf.security.UserRegistry#readAttributes(java.lang.String)
	 */
	@Override
	public Map<String, Object[]> readAttributes(String uniqueId)
			throws NamingException {

		DirContext ctx = null;
		try {
			ctx = getContext(getBindDN(), getBindPWD());
			Attributes attrs = ctx.getAttributes(uniqueId);
			Map<String, Object[]> map = extractAttributes(attrs);
			return map;
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (Throwable t) {
					// Nothing to do
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see am.ajf.security.UserRegistry#findGroups(java.lang.String)
	 */
	@Override
	public String[] findGroupsForUserUniqueId(String uniqueUserId) throws NamingException {

		String[] groups = new String[0];

		String personMemberOfAttribute = getUserMemberOfAttribute();
		if (!isNullOrEmpty(personMemberOfAttribute)) {
			try {
				Object[] res = readAttribute(uniqueUserId,
						personMemberOfAttribute);
				if ((null != res) && (res.length > 0)) {
					List<Object> objList = Arrays.asList(res);
					groups = objList.toArray(new String[0]);
				}

			} catch (Exception e) {
				String message = String.format(
						"Unable to read user groups attribute '%s'",
						personMemberOfAttribute);
				logger.log(Level.SEVERE, message, e);
				groups = null;
			}
		}

		if ((null == groups) || (0 == groups.length)) {

			String filter = String.format("(%s=%s)",
					getGroupMembersAttribute(), uniqueUserId);
			NamingEnumeration<SearchResult> results = doFilter(getGroupBase(),
					filter, getGroupSearchControlScopeValue(),
					new String[] { getGroupDNAttribute() });

			List<String> groupsList = new ArrayList<String>();
			while (results.hasMoreElements()) {
				SearchResult sr = results.nextElement();
				String key = sr.getAttributes().getIDs().nextElement();
				Attribute attr = sr.getAttributes().get(key);
				String groupDN = (String) attr.get();
				groupsList.add(groupDN);
			}
			if (!groupsList.isEmpty()) {
				groups = groupsList.toArray(new String[0]);
			}

		}

		if ((!isNullOrEmpty(getGroupNameAttribute()))) {
			DirContext ctx = null;
			try {
				String[] attrIds = new String[] { getGroupNameAttribute() };
				ctx = getContext(getBindDN(), getBindPWD());
				for (int i = 0; i < groups.length; i++) {
					String groupDN = groups[i];

					Attributes attrs = ctx.getAttributes(groupDN, attrIds);
					String key = attrs.getIDs().next();
					String groupName = (String) attrs.get(key).get();
					groups[i] = groupName; // .toLowerCase();
				}

			} finally {
				if (ctx != null) {
					try {
						ctx.close();
					} catch (Throwable t) {
						// Nothing to do
					}
				}
			}
		}

		return groups;
	}
	
	/*
	 * (non-Javadoc)
	 * @see am.ajf.security.spi.UserRegistry#findGroupsByName(java.lang.String)
	 */
	@Override
	public String[] findGroupsByName(String namePattern) throws NamingException {
		
		String[] groups = new String[0];
		
		String filter = String.format("(%s=%s*)",
				getGroupNameAttribute(), namePattern);
		NamingEnumeration<SearchResult> results = doFilter(getGroupBase(),
				filter, getGroupSearchControlScopeValue(),
				new String[] { getGroupDNAttribute() });

		List<String> groupsList = new ArrayList<String>();
		while (results.hasMoreElements()) {
			SearchResult sr = results.nextElement();
			String key = sr.getAttributes().getIDs().nextElement();
			Attribute attr = sr.getAttributes().get(key);
			String groupDN = (String) attr.get();
			groupsList.add(groupDN);
		}
		if (!groupsList.isEmpty()) {
			groups = groupsList.toArray(new String[0]);
		}
		
		return groups;
		
	}
	
	/* (non-Javadoc)
	 * @see am.ajf.security.spi.UserRegistry#findUsersForGroupeUniqueId(java.lang.String)
	 */
	@Override
	public String[] findUsersForGroupeUniqueId(String groupeUniqueId)
			throws NamingException {
		
		String[] users = new String[0];
		
		Object[] members = readAttribute(groupeUniqueId, getGroupMembersAttribute());
		if (null != members) {
			List<String> usersList = new ArrayList<String>();
			for (Object member : members) {
				usersList.add((String)member);
			}
			if (!usersList.isEmpty()) {
				users = usersList.toArray(new String[0]);
			}
		}
		
		return users;
		
	}

	private boolean isNullOrEmpty(String string) {
		boolean res = ((null == string) || (0 == string.trim().length()));
		return res;
	}


	
	/*
	public Collection<Map<String, Object[]>> findUsersByCriteria(
			String key,
			String criteria,
			String[] attributesNames)
			throws NamingException {

		Collection<Map<String, Object[]>> res = new ArrayList<Map<String,Object[]>>();

			String base = uidBaseDN;
			String filter =
				StringHelper.replace(uid2DNFilter, new String[] { key, criteria });

			NamingEnumeration results =
				doFilter(
					base,
					filter,
					attributesNames,
					LDAPHelper.giveSearchControlScope(
						getSearchControlScope(),
						this));

			try {
				// Boucle sur les entr�es trouv�es :
				while (results.hasMoreElements()) {
					SearchResult sr = (SearchResult) results.nextElement();

					String dn = sr.getName();
					// concat�nation du baseDN
					if ((null != dn) && (sr.isRelative())) {
						if (!StringHelper.isEmpty(base))
							dn = dn + "," + base;
					}

					Map attrsTable = extractAttributes(sr.getAttributes());
					attrsTable.put("dn", new String[] { dn });

					if (null == res)
						res = new ArrayList();
					res.add(attrsTable);

				}
			} catch (Exception exc) {
				LoggerHelper.error(this,"Exception while iterate on Persons", exc);
			
			} finally {
				results.close();
			}

			return res;

		}
	
	
	public Collection<Map<String, Object[]>> findGroupsByCriteria(
			String key,
			String criteria,
			String[] attributesNames)
			
			throws DirectoryLookupException, NamingException {

			Collection res = null;

			String base = gidBaseDN;
			String filter =
				StringHelper.replace(gid2DNFilter, new String[] { key, criteria });

			NamingEnumeration results =
				doFilter(
					base,
					filter,
					attributesNames,
					LDAPHelper.giveSearchControlScope(
						getSearchControlScope(),
						this));

			try {
				// Boucle sur les entr�es trouv�es :
				while (results.hasMoreElements()) {
					SearchResult sr = (SearchResult) results.nextElement();

					String dn = sr.getName();
					// concat�nation du baseDN
					if ((null != dn) && (sr.isRelative())) {
						if (!StringHelper.isEmpty(base))
							dn = dn + "," + base;
					}

					Map attrsTable = extractAttributes(sr.getAttributes());
					attrsTable.put("dn", new String[] { dn });

					if (null == res)
						res = new ArrayList();
					res.add(attrsTable);

				}
			} catch (Exception exc) {
				LoggerHelper.error(this,"Exception while iterate on Groups", exc);
			} finally {
				results.close();
			}

			return res;

		}
*/

}
