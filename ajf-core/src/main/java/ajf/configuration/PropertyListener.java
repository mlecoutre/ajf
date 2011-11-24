package ajf.configuration;

import org.apache.commons.configuration.Configuration;

public interface PropertyListener {

	/**
     * Notifies this listener about a manipulation on a monitored configuration property
     * object.
     *
	 * @param eventType
	 * @param source
	 * @param key
	 * @param oldValue
	 * @param newValue
	 */
	void configurationChanged(EnumEventType eventType, Configuration source, String key,
			Object oldValue, Object newValue);

	void configurationClear(EnumEventType enum1, Configuration source);

}
