package am.ajf.monitoring;

import am.ajf.monitoring.api.EventEmitter;
import am.ajf.monitoring.api.EventFilter;
import am.ajf.monitoring.api.EventFormatter;
import am.ajf.monitoring.exceptions.EventException;

import com.mycila.event.Event;
import com.mycila.event.Subscriber;

public final class EventHandler implements Subscriber<AbstractEvent> {

	private final EventFilter eventFilter;
	private final EventFormatter eventFormatter;
	private final EventEmitter eventEmitter;

	/**
	 * 
	 * @param eventFilter
	 * @param eventFormatter
	 * @param eventEmitter
	 */
	public EventHandler(EventFilter eventFilter, EventFormatter eventFormatter,
			EventEmitter eventEmitter) {
		super();
		this.eventFilter = eventFilter;
		this.eventFormatter = eventFormatter;
		this.eventEmitter = eventEmitter;
	}
	
	/**
	 * 
	 * @param eventFormatter
	 * @param eventEmitter
	 */
	public EventHandler(EventFormatter eventFormatter,
			EventEmitter eventEmitter) {
		super();
		this.eventFilter = null;
		this.eventFormatter = eventFormatter;
		this.eventEmitter = eventEmitter;
	}



	/**
	 * publish the event
	 * @param eventSource
	 * @throws EventException
	 */
	private void send(AbstractEvent eventSource) throws EventException {
		
		if (null != eventFilter) {
			boolean acceptedEvent = eventFilter.accept(eventSource);
			if (!acceptedEvent) {
				// log it?
				return;
			}
		}
		
		String formattedEvent = null;
		if (null != eventFormatter) {
			formattedEvent = eventFormatter.format(eventSource);
		}
		else {
			formattedEvent = eventSource.toString();
		}		
		eventEmitter.send(eventSource, formattedEvent);
		
	}

	@Override
	public void onEvent(Event<AbstractEvent> event) throws Exception {
			
		send(event.getSource());
		
	}

}
