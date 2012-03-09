package am.ajf.monitoring.api;

import am.ajf.monitoring.AbstractEvent;
import am.ajf.monitoring.exceptions.EventFormatterException;

public interface EventFormatter {

	String format(AbstractEvent eventSource) throws EventFormatterException;

}
