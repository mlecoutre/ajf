package am.ajf.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;


public class PropertiesGroupsMappingUserRoleResolver extends
		AbstractGroupsMappingUserRoleResolver {

	private final Logger logger = Logger
			.getLogger(PropertiesGroupsMappingUserRoleResolver.class.getName());

	protected String rolesGroupsMappingPath = null;

	public PropertiesGroupsMappingUserRoleResolver() {
		super();
	}

	public String getRolesGroupsMappingPath() {
		return rolesGroupsMappingPath;
	}

	public void setRolesGroupsMappingPath(String rolesGroupsMappingPath) {
		this.rolesGroupsMappingPath = rolesGroupsMappingPath;
	}

	@Override
	protected Set<RoleMapping> loadRolesMapping() throws Exception {

		Set<RoleMapping> rolesMappingSet = new HashSet<RoleMapping>();

		if (null == rolesGroupsMappingPath) {
			logger.warning("The property 'rolesGroupsMappingPath' can not be null.");
			return rolesMappingSet;
		}

		InputStream inputStream = null;
		try {
			
			File rolesGroupsMappingFile = new File(rolesGroupsMappingPath);
			if (rolesGroupsMappingFile.exists()) {
				inputStream = new FileInputStream(rolesGroupsMappingFile);
			} else {
				inputStream = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(rolesGroupsMappingPath);
			}

			if (null == inputStream)
				return rolesMappingSet;
			
			Map<String, RoleMapping> rolesMapping = new HashMap<String, RoleMapping>();
	
			Properties properties = new Properties();
			properties.load(inputStream);
	
			Set<Object> keys = properties.keySet();
			for (Object objKey : keys) {
				String key = (String) objKey;
	
				if (key.contains(".")) {
	
					String[] strParts = key.split(".");
					String roleName = strParts[0];
					String specialMapping = strParts[1];
	
					RoleMapping roleMapping = getRoleMapping(rolesMapping, key,
							roleName);
	
					if ((specialMapping.equalsIgnoreCase("everyone"))) {
						boolean everyone = Boolean.parseBoolean((String) properties
								.get(key));
						roleMapping.setEveryone(everyone);
					} else {
	
						if ((specialMapping
								.equalsIgnoreCase("allAuthenticatedUsers"))) {
							boolean allAuthenticatedUsers = Boolean
									.parseBoolean((String) properties.get(key));
							roleMapping
									.setAllAuthenticatedUsers(allAuthenticatedUsers);
						}
	
					}
	
				} else {
					// it is role name
					String roleName = key;
	
					RoleMapping roleMapping = getRoleMapping(rolesMapping, key,
							roleName);
	
					String value = properties.getProperty(key);
					if ((null != value) && (value.length() > 0)) {
						Collection<String> groups = new ArrayList<String>();
	
						String[] mappedGroups = value.split("[ ]*,[ ]*");
						for (String mappedGroup : mappedGroups) {
							if ((mappedGroup.trim().length() > 0)) {
								groups.add(mappedGroup.trim());
							}
						}
						if (!groups.isEmpty()) {
							roleMapping.setMappedGroups(groups);
						}
	
					}
	
				}
	
			}
	
			if (rolesMapping.isEmpty()) {
				return null;
			} else {
				rolesMappingSet.addAll(rolesMapping.values());
				return rolesMappingSet;
			}
		
		}
		finally {
			if (null != inputStream) {
				try {
					inputStream.close();
					inputStream = null;
				} catch (Exception e) {
					// Nothing to do
				}
			}
		}

	}

	protected RoleMapping getRoleMapping(Map<String, RoleMapping> rolesMapping,
			String key, String roleName) {
		RoleMapping roleMapping = null;
		if (rolesMapping.containsKey(key)) {
			roleMapping = rolesMapping.get(key);
		} else {
			roleMapping = new RoleMapping(roleName);
			rolesMapping.put(key, roleMapping);
		}
		return roleMapping;
	}

}
