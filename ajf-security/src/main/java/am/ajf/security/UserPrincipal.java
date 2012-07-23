package am.ajf.security;

import java.io.Serializable;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

public class UserPrincipal implements Principal, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String name;
	private final String fullName;
	private final boolean trusted;
	private String displayName = null;
	private String mailAddress = null;

	private final Map<String, Object> attributesMap = new HashMap<String, Object>();

	public UserPrincipal(String name, boolean trusted, String fullName) {
		super();
		this.name = name;
		this.trusted = trusted;
		this.fullName = fullName;
	}
	
	public UserPrincipal(String name, String fullName) {
		super();
		this.name = name;
		this.trusted = false;
		this.fullName = fullName;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public String getFullName() {
		return fullName;
	}

	public boolean isTrusted() {
		return trusted;
	}

	public Map<String, Object> getAttributes() {
		return attributesMap;
	}

	public void setAttribute(String attributeName, Object values) {
		attributesMap.put(attributeName, values);
	}

	public Object getAttribute(String attributeName) {
		if (attributesMap.containsKey(attributeName)) {
			return attributesMap.get(attributeName);
		}
		return null;
	}

	public Object removeAttribute(String attributeName) {
		if (attributesMap.containsKey(attributeName)) {
			Object attrValue = attributesMap.remove(attributeName);
			return attrValue;
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserPrincipal other = (UserPrincipal) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("UserPrincipal [name=%s, trusted=%s, fullName=%s, displayName=%s, mailAddress=%s]",
						name, trusted, fullName, displayName, mailAddress);
	}

}
