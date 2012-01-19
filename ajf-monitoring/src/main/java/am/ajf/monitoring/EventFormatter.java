package am.ajf.monitoring;

import am.ajf.monitoring.exceptions.EventFormatterException;

public interface EventFormatter {

	String format(Object eventSource) throws EventFormatterException;

}
