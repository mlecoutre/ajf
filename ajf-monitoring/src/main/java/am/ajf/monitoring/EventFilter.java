package am.ajf.monitoring;

import am.ajf.monitoring.exceptions.EventFilterException;

public interface EventFilter {

	boolean accept(Object eventSource) throws EventFilterException;

}
