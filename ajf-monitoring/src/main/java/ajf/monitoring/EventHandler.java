package ajf.monitoring;

import ajf.monitoring.exceptions.EventException;

import com.mycila.event.Event;
import com.mycila.event.Subscriber;

public final class EventHandler implements Subscriber<AbstractEvent> {

	private final EventFilter eventFilter;
	private final EventFormatter eventFormatter;
	private final EventEmitter eventEmitter;

	public EventHandler(EventFilter eventFilter, EventFormatter eventFormatter,
			EventEmitter eventEmitter) {
		
		super();
		
		this.eventFilter = eventFilter;
		this.eventFormatter = eventFormatter;
		this.eventEmitter = eventEmitter;
	}
	
	/**
	 * publish the event
	 * @param eventSource
	 * @throws EventException
	 */
	private void publish(AbstractEvent eventSource) throws EventException {
		
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
			
		publish(event.getSource());
		
	}

}
