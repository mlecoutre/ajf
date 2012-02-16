package am.ajf.monitoring;

import am.ajf.monitoring.exceptions.EventEmitterException;

public interface EventEmitter {

	void send(AbstractEvent eventSource, String formattedEvent)
			throws EventEmitterException;

}
