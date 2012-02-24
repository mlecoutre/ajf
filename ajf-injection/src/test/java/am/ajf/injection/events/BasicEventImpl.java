package am.ajf.injection.events;

public class BasicEventImpl implements Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String eventName;
	
	public BasicEventImpl() {
		super();
	}

	public BasicEventImpl(String eventName) {
		super();
		this.eventName = eventName;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	@Override
	public String toString() {
		return "AnEvent [eventName=" + eventName + "]";
	}
	
}
