package am.ajf.web.dto;

import java.io.Serializable;

/**
 * Logger object to display on the web
 * 
 * @author U002617
 * 
 */
public class LoggerVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name = null;
	private String level = null;
	private boolean additive = false;

	/**
	 * Default constructor
	 */
	public LoggerVO() {
		super();
	}

	/**
	 * Standard constructor
	 * 
	 * @param name
	 *            logger name
	 * @param level
	 *            logger level
	 * @param additive
	 *            if additive
	 */
	public LoggerVO(String name, String level, boolean additive) {
		super();
		this.name = name;
		this.level = level;
		this.additive = additive;
	}

	/**
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 *            logger name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * 
	 * @param level
	 *            loggerlevel
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * 
	 * @return boolean value
	 */
	public boolean isAdditive() {
		return additive;
	}

	/**
	 * 
	 * @param additive
	 *            set additivity
	 */
	public void setAdditive(boolean additive) {
		this.additive = additive;
	}

	@Override
	public String toString() {
		return "LoggerVO [name=" + name + ", level=" + level + ", additive="
				+ additive + "]";
	}

}
