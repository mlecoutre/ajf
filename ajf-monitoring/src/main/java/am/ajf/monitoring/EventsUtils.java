package am.ajf.monitoring;

public class EventsUtils {

	private EventsUtils() {
		super();
	}
	
	/**
	 * 
	 * @param eventClass
	 * @return the event type name
	 */
	public static String getEventType(Class<?> eventClass) {
		return eventClass.getSimpleName();
	}

}
