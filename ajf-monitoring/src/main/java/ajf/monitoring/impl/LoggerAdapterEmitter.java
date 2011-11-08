package ajf.monitoring.impl;

import org.slf4j.Logger;

import ajf.monitoring.AbstractEvent;
import ajf.monitoring.EventEmitter;
import ajf.monitoring.exceptions.EventEmitterException;

public class LoggerAdapterEmitter implements EventEmitter {
	
	private final Logger logger;

	public LoggerAdapterEmitter(Logger logger) {
		super();
		this.logger = logger;
	}

	@Override
	public void send(AbstractEvent eventSource, String formattedEvent)
			throws EventEmitterException {
		
		logger.info(formattedEvent);
		
	}

}
