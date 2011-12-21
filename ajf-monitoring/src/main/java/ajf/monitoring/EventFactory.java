package ajf.monitoring;

import static ajf.monitoring.EventUtils.getEventType;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import ajf.logger.LoggerFactory;
import ajf.monitoring.exceptions.DuplicateEventTypeException;
import ajf.utils.BeanUtils;

public class EventFactory {
	
	private final static Map<String, Class<?>> eventsTypesMap = new HashMap<String, Class<?>>();
	
	private final static Logger logger = LoggerFactory.getLogger(EventFactory.class);

	/**
	 * default constructor
	 */
	private EventFactory() {
		super();
	}
		
	/**
	 * 
	 * @param eventClass
	 * @throws DuplicateEventTypeException
	 */
	public static void registerEvent(
			Class<?> eventClass)
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
			Class<?> eventClass)
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
	public static <E> E newEvent(String eventType) {
		
		Object evt = null;
		if (eventsTypesMap.containsKey(eventType)) {
			Class<?> eventClass = eventsTypesMap
					.get(eventType);
			evt = BeanUtils.newInstance(eventClass);
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
	public static <E> E newEvent(Class<?> eventClass) {
		
		String eventType = getEventType(eventClass);
		Object evt = newEvent(eventType);
		return (E)evt;
		
	}
	
}
