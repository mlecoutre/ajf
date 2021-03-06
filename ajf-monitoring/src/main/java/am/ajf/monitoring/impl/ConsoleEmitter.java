package am.ajf.monitoring.impl;

import java.io.PrintStream;

import am.ajf.monitoring.AbstractEvent;
import am.ajf.monitoring.api.EventEmitter;
import am.ajf.monitoring.exceptions.EventEmitterException;


public class ConsoleEmitter implements EventEmitter {

	private final PrintStream printStream;
	
	/**
	 * 
	 */
	public ConsoleEmitter() {
		super();
		printStream = System.out;
	}

	/**
	 * @param printStream
	 */
	public ConsoleEmitter(PrintStream printStream) {
		super();
		this.printStream = printStream;
	}
	

	@Override
	public void send(AbstractEvent eventSource, String formattedEvent) throws EventEmitterException {
		printStream.println(formattedEvent);	
	}

}