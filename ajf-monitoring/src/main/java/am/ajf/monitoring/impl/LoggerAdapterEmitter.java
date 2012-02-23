package am.ajf.monitoring.impl;

import org.slf4j.Logger;

import am.ajf.monitoring.AbstractEvent;
import am.ajf.monitoring.EventEmitter;
import am.ajf.monitoring.exceptions.EventEmitterException;

public class LoggerAdapterEmitter implements EventEmitter {
	
	private final Logger logger;

	public LoggerAdapterEmitter(Logger logger) {
		super();
		this.logger = logger;
	}

	@Override
	public void send(AbstractEvent eventSource, String formattedEvent)
			throws EventEmitterException {
		
		logger.trace(formattedEvent);
		
	}

}
