package ajf.monitoring;

public class EventUtils {

	private EventUtils() {
		super();
	}
	
	/**
	 * 
	 * @param eventClass
	 * @return the event type name
	 */
	public static String getEventType(Class<? extends AbstractEvent> eventClass) {
		return eventClass.getName();
	}

}
