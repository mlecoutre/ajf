package am.ajf.monitoring.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import am.ajf.monitoring.AbstractEvent;

public class DynaEvent extends AbstractEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final Map<String, Object> delegateMap;
	
	public DynaEvent() {
		super();
		delegateMap = new HashMap<String, Object>();
		
	}

	public int size() {
		return delegateMap.size();
	}

	public boolean isEmpty() {
		return delegateMap.isEmpty();
	}

	public boolean containsKey(Object key) {
		return delegateMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return delegateMap.containsValue(value);
	}

	public Object get(Object key) {
		return delegateMap.get(key);
	}

	public Object put(String key, Object value) {
		return delegateMap.put(key, value);
	}

	public Object remove(Object key) {
		return delegateMap.remove(key);
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		delegateMap.putAll(m);
	}

	public void clear() {
		delegateMap.clear();
	}

	public Set<String> keySet() {
		return delegateMap.keySet();
	}

	public Collection<Object> values() {
		return delegateMap.values();
	}

	public Set<Entry<String, Object>> entrySet() {
		return delegateMap.entrySet();
	}

	public boolean equals(Object o) {
		return delegateMap.equals(o);
	}

	public int hashCode() {
		return delegateMap.hashCode();
	}

}
