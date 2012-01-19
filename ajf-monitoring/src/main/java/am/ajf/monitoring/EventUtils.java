package am.ajf.monitoring;

public class EventUtils {

	private EventUtils() {
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
