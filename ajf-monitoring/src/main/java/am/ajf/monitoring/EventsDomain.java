package am.ajf.monitoring;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;

import am.ajf.core.logger.LoggerFactory;
import am.ajf.monitoring.exceptions.EventException;

import com.mycila.event.Dispatcher;
import com.mycila.event.Dispatchers;
import com.mycila.event.ErrorHandler;
import com.mycila.event.Event;
import com.mycila.event.Subscription;
import com.mycila.event.Topic;
import com.mycila.event.Topics;

public class EventsDomain implements ErrorHandler, Closeable {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final Dispatcher eventDispatcher;
	private EventHandler defaultEventHandler = null;
	private boolean closed = true;

	/**
	 * default constructor
	 */
	public EventsDomain() {
		super();
		/* init section */
		eventDispatcher = Dispatchers.asynchronousSafe(this);
		//eventDispatcher = Dispatchers.synchronousSafe(this);
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
	 * send event
	 * @param event
	 */
	public void sendEvent(AbstractEvent event) {
		String eventType = event.getEventType();
		sendEvent(eventType, event);		
	}
	
	/**
	 * send event
	 * @param eventType
	 * @param event
	 */
	private void sendEvent(String eventType, Object event) {
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
	public void setDefaultEventHandler(EventHandler eventHandler) {
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
			eventDispatcher.subscribe(Topics.any(), Object.class,
					eventHandler);
			defaultEventHandler = eventHandler;
			
		} else {
			
			Topics[] matchers = new Topics[eventTypes.length];
			for (int i = 0; i < eventTypes.length; i++) {
				String eventType = eventTypes[i];
				matchers[i] = Topics.only(eventType);
			}
			eventDispatcher.subscribe(Topics.anyOf(matchers),
					Object.class, eventHandler);
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

		logger.warn("Receive exception '" + exc.getMessage() + "' for event '"
				+ event.getSource().toString() + "' of type '" + event.getTopic().getName()
				+ "' at " + event.nanoTime(), exc);

	}

}
