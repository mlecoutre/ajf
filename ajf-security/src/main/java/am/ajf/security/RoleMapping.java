package am.ajf.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class RoleMapping implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String roleName;

	private boolean everyone = false;
	private boolean allAuthenticatedUsers = false;

	private final Collection<String> mappedGroups = new ArrayList<String>();

	public RoleMapping(String roleName) {
		super();
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setEveryone(boolean everyone) {
		this.everyone  = everyone;		
	}

	public void setAllAuthenticatedUsers(boolean allAuthenticatedUsers) {
		this.allAuthenticatedUsers = allAuthenticatedUsers;		
	}

	public void setMappedGroups(Collection<String> groups) {
		this.mappedGroups.clear();
		this.mappedGroups.addAll(groups);
	}

	public Collection<String> getMappedGroups() {
		return mappedGroups;
	}

	public boolean isEveryone() {
		return everyone;
	}

	public boolean isAllAuthenticatedUsers() {
		return allAuthenticatedUsers;
	}

	@Override
	public String toString() {
		return "RoleMapping [roleName=" + roleName + ", everyone="
				+ everyone + ", allAuthenticatedUsers=" + allAuthenticatedUsers
				+ ", groups=" + mappedGroups + "]";
	}

	public boolean containGroup(String group) {
		if (mappedGroups.isEmpty()) {
			return false;
		}
		for (String mappedGroup : mappedGroups) {
			if (mappedGroup.equalsIgnoreCase(group)) {
				return true;
			}
		}		
		return false;
	}

	

}
