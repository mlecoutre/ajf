package am.ajf.security.jaas;

import java.io.Serializable;
import java.security.Principal;

public class SimpleUserPrincipal implements Principal, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String name;
	
	public SimpleUserPrincipal(String userName) {
		super();
		this.name = userName;
	}

	public String getName() {
		return name;
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
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SimpleUserPrincipal other = (SimpleUserPrincipal) obj;
		if (name == null) {
			if (other.name != null) return false;
		}
		else
			if (!name.equals(other.name)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "SimpleUserPrincipal [name=" + name + "]";
	}
	
}
