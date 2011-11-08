package ajf.monitoring;

import java.io.Serializable;


/**
 * abstract event
 * 
 * @author U002617
 */
public abstract class AbstractEvent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String uuid;

	/**
	 * 
	 */
	public AbstractEvent() {
		super();
	}
	
	/**
	 * @param uuid
	 */
	public AbstractEvent(String uuid) {
		super();
		this.uuid = uuid;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	public String getUUID() {
		return uuid;
	}

}
