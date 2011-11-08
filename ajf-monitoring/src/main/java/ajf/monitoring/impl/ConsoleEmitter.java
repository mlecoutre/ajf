package ajf.monitoring.impl;

import java.io.PrintStream;

import ajf.monitoring.AbstractEvent;
import ajf.monitoring.EventEmitter;
import ajf.monitoring.exceptions.EventEmitterException;


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
	public void send(AbstractEvent source, String formattedEvent) throws EventEmitterException {
		printStream.println(formattedEvent);	
	}

}