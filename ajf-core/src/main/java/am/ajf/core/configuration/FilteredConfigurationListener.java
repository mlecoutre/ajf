package am.ajf.core.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;

public class FilteredConfigurationListener implements ConfigurationListener {
	
	private Map<String, Object> aggregatedMap = new HashMap<String, Object>();
	
	private final List<String> listenedKeys;
	private final PropertyListener propertyListener;
	
	public FilteredConfigurationListener(List<String> listenedKeys, PropertyListener propertyListener) {
		super();
		this.listenedKeys = listenedKeys;
		this.propertyListener = propertyListener;
	}

	public List<String> getListenedKeys() {
		return listenedKeys;
	}

	@Override
	public synchronized void configurationChanged(ConfigurationEvent event) {
		
		if (!(event.getSource() instanceof Configuration)) {
			return;
		}
		
		String key = event.getPropertyName();
		boolean isClear = (event.getType() == AbstractConfiguration.EVENT_CLEAR);
		
		if (!listenedKeys.contains(key) && (!isClear))
			return;
		
		Configuration source = (Configuration) event.getSource();
		if (isClear) {
			if (!event.isBeforeUpdate()) {
				propertyListener.configurationClear(EnumEventType.getEnum(event.getType()), source);
			}
			return;
		}
		
		if (event.isBeforeUpdate()) {
			Object oldProperty = source.getProperty(key);
			aggregatedMap.put(key, oldProperty);
		}
		else {
			Object oldValue = aggregatedMap.remove(key);
			Object value = event.getPropertyValue();
			propertyListener.configurationChanged(EnumEventType.getEnum(event.getType()) ,source, key, oldValue, value);
		}
		
	}

}
