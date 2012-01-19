package am.ajf.security.jaas;

import java.io.Serializable;
import java.security.Principal;

public class SimpleRolePrincipal  implements Principal, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String name;
	
	public SimpleRolePrincipal(String roleName) {
		super();
		this.name = roleName;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "SimpleRolePrincipal [name=" + name + "]";
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
		SimpleRolePrincipal other = (SimpleRolePrincipal) obj;
		if (name == null) {
			if (other.name != null) return false;
		}
		else
			if (!name.equals(other.name)) return false;
		return true;
	}
	

}
