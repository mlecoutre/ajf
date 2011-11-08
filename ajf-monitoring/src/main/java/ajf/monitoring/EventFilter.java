package ajf.monitoring;

import ajf.monitoring.exceptions.EventFilterException;

public interface EventFilter {

	boolean accept(AbstractEvent event) throws EventFilterException;

}
