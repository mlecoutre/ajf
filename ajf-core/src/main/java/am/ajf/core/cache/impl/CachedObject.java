package am.ajf.core.cache.impl;

import java.io.Serializable;

public class CachedObject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Object value;
	private transient long lastAccess;
	
	public CachedObject() {
		super();
	}

	public CachedObject(Object value) {
		super();
		setValue(value);
	}

	public Object getValue() {
		this.lastAccess = System.currentTimeMillis();
		return value;
	}

	public void setValue(Object value) {
		this.lastAccess = System.currentTimeMillis();
		this.value = value;		
	}

	public long getLastAccess() {
		return lastAccess;		
	}

	public void updateLastAccess() {
		this.lastAccess = System.currentTimeMillis();		
	}
	
}
