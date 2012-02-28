package am.ajf.web.dto;

import java.io.Serializable;

public class LoggerVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name = null;
	private String level = null;
	private boolean additive = false;
	
	public LoggerVO() {
		super();
	}
	
	public LoggerVO(String name, String level, boolean additive) {
		super();
		this.name = name;
		this.level = level;
		this.additive = additive;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public boolean isAdditive() {
		return additive;
	}

	public void setAdditive(boolean additive) {
		this.additive = additive;
	}
	
}
