package am.ajf.monitoring.api;

import am.ajf.monitoring.AbstractEvent;
import am.ajf.monitoring.exceptions.EventFilterException;

public interface EventFilter {

	boolean accept(AbstractEvent eventSource) throws EventFilterException;

}
