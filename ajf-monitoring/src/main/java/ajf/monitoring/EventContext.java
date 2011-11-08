package ajf.monitoring;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import ajf.logger.LoggerFactory;
import ajf.monitoring.exceptions.DuplicateEventTypeException;
import ajf.utils.BeanUtils;

import com.mycila.event.Dispatcher;
import com.mycila.event.Dispatchers;
import com.mycila.event.ErrorHandler;
import com.mycila.event.Event;
import com.mycila.event.Subscription;
import com.mycila.event.Topic;
import com.mycila.event.Topics;

public class EventContext implements ErrorHandler, Closeable {
	
	private static final EventContext instance = new EventContext();
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final Map<String, Class<? extends AbstractEvent>> eventsTypesMap = new HashMap<String, Class<? extends AbstractEvent>>();
	
	private final Dispatcher eventDispatcher;
	private EventHandler defaultEventHandler = null;

	/* default hidden constructor */

	/**
	 * 
	 */
	private EventContext() {
		super();
		
		eventDispatcher = Dispatchers.asynchronousSafe(this);
				
	}

	/**
	 * 
	 * @return the singleton
	 */
	public static EventContext getInstance() {
		return instance;
	}

	/**
	 * 
	 * @param eventClass
	 * @throws DuplicateEventTypeException
	 */
	public synchronized void registerEvent(
			Class<? extends AbstractEvent> eventClass)
			throws DuplicateEventTypeException {

		String eventType = eventClass.getName();

		if (eventsTypesMap.containsKey(eventType)) {
			throw new DuplicateEventTypeException("The event type '"
					+ eventType + "' already exist.");
		}
		eventsTypesMap.put(eventType, eventClass);

	}
	
	/**
	 * 
	 * @param eventType
	 * @return a new event
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public <E extends AbstractEvent> E newEvent(String eventType) {

		E evt = null;
		if (eventsTypesMap.containsKey(eventType)) {
			Class<? extends AbstractEvent> eventClass = eventsTypesMap
					.get(eventType);
			// replace by BeanUtils one
			evt = (E) BeanUtils.getInstance().newInstance(eventClass);
		}

		return (E) evt;
	}
	
	/**
	 * send event
	 * @param event
	 */
	public void sendEvent(AbstractEvent event) {
		try {
			String eventType = event.getClass().getName();
			eventDispatcher.publish(Topic.topic(eventType), event);
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			logger.error("Exception while trying to send event of type: ".concat(event.getClass().getName()), e);
			throw new RuntimeException(e);			
		}
	}
	
	/**
	 * install events handler
	 * 
	 * @param eventCategory
	 * @param eventHandler
	 */
	public void installEventHandler(
			Class<? extends AbstractEvent>[] eventTypes,
			EventHandler eventHandler) {

		subscribe(eventTypes, eventHandler);

	}
	
	/**
	 * install default event handler
	 * 
	 * @param eventHandler
	 */
	public void installDefaultEventHandler(EventHandler eventHandler) {

		subscribe(null, eventHandler);

	}
	
	/**
	 * uninstall event handler
	 * @param eventHandler
	 */
	public void uninstallEventHandler(EventHandler eventHandler) {

		unsubscribe(eventHandler);

	}
	
	
	/**
	 * install eventHandler
	 * 
	 * @param eventCategory
	 * @param eventHandler
	 */
	private synchronized void subscribe(
			Class<? extends AbstractEvent>[] eventTypes,
			EventHandler eventHandler) {

		if ((null == eventTypes) || (0 == eventTypes.length)) {
			
			if (null != defaultEventHandler) {
				unsubscribe(eventHandler);
				defaultEventHandler = null;
			}
			eventDispatcher.subscribe(Topics.any(), AbstractEvent.class,
					eventHandler);
			defaultEventHandler = eventHandler;
			
		} else {
			
			Topics[] matchers = new Topics[eventTypes.length];
			for (int i = 0; i < eventTypes.length; i++) {
				String eventType = eventTypes[i].getSimpleName();
				matchers[i] = Topics.only(eventType);
			}
			eventDispatcher.subscribe(Topics.anyOf(matchers),
					AbstractEvent.class, eventHandler);
		}

	}
	
	/**
	 * uninstall eventHandler
	 * 
	 * @param eventHandler
	 */
	private synchronized void unsubscribe(EventHandler eventHandler) {

		eventDispatcher.unsubscribe(eventHandler);

	}
	
	@Override
	public void close() throws IOException {
		
		try {
			eventDispatcher.close();
		} catch (Exception e) {
			throw new IOException(e);
		}
		
	}

	@Override
	public <E> void onError(Subscription<E> subscription, Event<E> event,
			Exception exc) {

		logger.error("Receive exception '" + exc.getMessage() + "' for event '"
				+ event.getSource().toString() + "' of type '" + event.getTopic().getName()
				+ "' at " + event.nanoTime(), exc);

	}

}
