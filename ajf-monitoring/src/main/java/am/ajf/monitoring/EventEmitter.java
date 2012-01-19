package am.ajf.monitoring;

import am.ajf.monitoring.exceptions.EventEmitterException;

public interface EventEmitter {

	void send(Object eventSource, String formattedEvent)
			throws EventEmitterException;

}
