package am.ajf.showcase.lib.business.dto;

import java.io.Serializable;

/**
 * Fire employee result
 * 
 * @author E010925
 * 
 */
public class FireEmployeeRB implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean removed;

	public FireEmployeeRB() {
		super();
	}

	public FireEmployeeRB(boolean isRemoved) {
		super();
		this.removed = isRemoved;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

}
