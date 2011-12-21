package ajf.monitoring;

import ajf.monitoring.exceptions.EventFilterException;

public interface EventFilter {

	boolean accept(Object eventSource) throws EventFilterException;

}
