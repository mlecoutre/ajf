package am.ajf.showcase.lib.business.dto;

import java.io.Serializable;

/**
 * Result of HireEmployee
 * 
 * @author E010925
 * 
 */
public class HireEmployeeRB implements Serializable {

	private static final long serialVersionUID = 1L;
	private boolean hired;

	public HireEmployeeRB() {
		super();
	}

	public HireEmployeeRB(boolean hired) {
		super();
		this.hired = hired;
	}

	public boolean isHired() {
		return hired;
	}

	public void setHired(boolean hired) {
		this.hired = hired;
	}

}
