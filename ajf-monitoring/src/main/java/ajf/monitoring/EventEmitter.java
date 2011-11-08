package ajf.monitoring;

import ajf.monitoring.exceptions.EventEmitterException;

public interface EventEmitter {

	void send(AbstractEvent eventSource, String formattedEvent)
			throws EventEmitterException;

}
