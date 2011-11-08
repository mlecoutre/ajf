package ajf.monitoring;

import ajf.monitoring.exceptions.EventFormatterException;

public interface EventFormatter {

	String format(AbstractEvent event) throws EventFormatterException;

}
