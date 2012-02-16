package am.ajf.monitoring;

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
	
	protected String eventType;
	
	/**
	 * 
	 */
	public AbstractEvent() {
		super();
		
		// set the default event type
		this.eventType = EventsUtils.getEventType(this.getClass());
		
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
}
