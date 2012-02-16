package am.ajf.monitoring;

import static am.ajf.monitoring.EventsUtils.getEventType;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import am.ajf.core.logger.LoggerFactory;
import am.ajf.core.utils.BeanUtils;
import am.ajf.monitoring.exceptions.DuplicateEventTypeException;

public class EventsFactory {
	
	private final static Map<String, Class<?>> eventsTypesMap = new HashMap<String, Class<?>>();
	
	private final static Logger logger = LoggerFactory.getLogger(EventsFactory.class);

	/**
	 * default constructor
	 */
	private EventsFactory() {
		super();
	}
		
	/**
	 * 
	 * @param eventClass
	 * @throws DuplicateEventTypeException
	 */
	public static void registerEvent(
			Class<? extends AbstractEvent> eventClass)
			throws DuplicateEventTypeException {
		String eventType = getEventType(eventClass);
		registerEvent(eventType, eventClass);
	}
	
	/**
	 * 
	 * @param eventType
	 * @param eventClass
	 * @throws DuplicateEventTypeException
	 */
	public static synchronized void registerEvent(
			String eventType,
			Class<? extends AbstractEvent> eventClass)
			throws DuplicateEventTypeException {
		
		if (eventsTypesMap.containsKey(eventType)) {
			throw new DuplicateEventTypeException("The event type '"
					+ eventType + "' already exist.");
		}
		logger.info("Register event class '".concat(eventClass.getName()).concat("' as: ").concat(eventType));
		eventsTypesMap.put(eventType, eventClass);

	}
	
	/**
	 * 
	 * @param eventType
	 * @return a new event
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static <E extends AbstractEvent> E newEvent(String eventType) {
		
		AbstractEvent evt = null;
		if (eventsTypesMap.containsKey(eventType)) {
			Class<?> eventClass = eventsTypesMap
					.get(eventType);
			evt = (AbstractEvent) BeanUtils.newInstance(eventClass);
			evt.setEventType(eventType);
		}

		return (E)evt;
		
	}
	
	/**
	 * 
	 * @param <E>
	 * @param eventClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E extends AbstractEvent> E newEvent(Class<?> eventClass) {
		String eventType = getEventType(eventClass);
		Object evt = newEvent(eventType);
		return (E)evt;
		
	}
	
}
