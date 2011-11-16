package ajf.monitoring;

import static ajf.monitoring.EventUtils.getEventType;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import ajf.logger.LoggerFactory;
import ajf.monitoring.exceptions.DuplicateEventTypeException;
import ajf.monitoring.exceptions.EventException;
import ajf.utils.BeanUtils;

import com.mycila.event.Dispatcher;
import com.mycila.event.Dispatchers;
import com.mycila.event.ErrorHandler;
import com.mycila.event.Event;
import com.mycila.event.Subscription;
import com.mycila.event.Topic;
import com.mycila.event.Topics;

public class EventManager implements ErrorHandler, Closeable {
	
	private final Map<String, Class<? extends AbstractEvent>> eventsTypesMap = new HashMap<String, Class<? extends AbstractEvent>>();
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final Dispatcher eventDispatcher;
	private EventHandler defaultEventHandler = null;
	private boolean closed = true;

	/**
	 * default constructor
	 */
	public EventManager() {
		super();
		/* init section */
		eventDispatcher = Dispatchers.asynchronousSafe(this);
		closed = false;
	}
	
	public boolean isClosed() {
		return closed;
	}
	
	private void checkClosed() {
		if (closed) {
			throw new EventException("The eventManage is closed.");
		}
	}

	/**
	 * 
	 * @param eventClass
	 * @throws DuplicateEventTypeException
	 */
	public void registerEvent(
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
	public synchronized void registerEvent(
			String eventType,
			Class<? extends AbstractEvent> eventClass)
			throws DuplicateEventTypeException {
		
		checkClosed();

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

		checkClosed();
		
		E evt = null;
		if (eventsTypesMap.containsKey(eventType)) {
			Class<? extends AbstractEvent> eventClass = eventsTypesMap
					.get(eventType);
			evt = (E) BeanUtils.newInstance(eventClass);
		}

		return evt;
	}
	
	/**
	 * 
	 * @param <E>
	 * @param eventClass
	 * @return
	 */
	public <E extends AbstractEvent> E newEvent(Class<? extends AbstractEvent> eventClass) {
		String eventType = getEventType(eventClass);
		@SuppressWarnings("unchecked")
		E result = (E) newEvent(eventType);
		return result;
	}
	
	/**
	 * send event
	 * @param event
	 */
	public void sendEvent(AbstractEvent event) {
		String eventType = getEventType(event.getClass());
		sendEvent(eventType, event);		
	}
	
	/**
	 * send event
	 * @param eventType
	 * @param event
	 */
	public void sendEvent(String eventType, AbstractEvent event) {
		try {
			checkClosed();
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
	 * @param eventTypes
	 * @param eventHandler
	 */
	public void installEventHandler(
			String[] eventTypes,
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
			String[] eventTypes,
			EventHandler eventHandler) {
		
		checkClosed();

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
				String eventType = eventTypes[i];
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

		checkClosed();
		eventDispatcher.unsubscribe(eventHandler);

	}
	
	@Override
	public void close() throws IOException {
		
		try {
			checkClosed();
			eventDispatcher.close();
			closed = true;
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
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
