package am.ajf.monitoring;

import am.ajf.monitoring.exceptions.EventFilterException;

public interface EventFilter {

	boolean accept(AbstractEvent eventSource) throws EventFilterException;

}
