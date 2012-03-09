package am.ajf.monitoring.api;

import am.ajf.monitoring.AbstractEvent;
import am.ajf.monitoring.exceptions.EventEmitterException;

public interface EventEmitter {

	void send(AbstractEvent eventSource, String formattedEvent)
			throws EventEmitterException;

}
