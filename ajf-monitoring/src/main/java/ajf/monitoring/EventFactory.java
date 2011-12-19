package ajf.monitoring;

import static ajf.monitoring.EventUtils.getEventType;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import ajf.logger.LoggerFactory;
import ajf.monitoring.exceptions.DuplicateEventTypeException;
import ajf.utils.BeanUtils;

public class EventFactory {
	
	private final static Map<String, Class<? extends AbstractEvent>> eventsTypesMap = new HashMap<String, Class<? extends AbstractEvent>>();
	
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
	public static <E extends AbstractEvent> E newEvent(String eventType) {
		
		E evt = null;
		if (eventsTypesMap.containsKey(eventType)) {
			Class<? extends AbstractEvent> eventClass = eventsTypesMap
					.get(eventType);
			evt = BeanUtils.newInstance(eventClass);
		}

		return evt;
	}
	
	/**
	 * 
	 * @param <E>
	 * @param eventClass
	 * @return
	 */
	public static <E extends AbstractEvent> E newEvent(Class<? extends AbstractEvent> eventClass) {
		String eventType = getEventType(eventClass);
		E result =  newEvent(eventType);
		return result;
	}
	
}
