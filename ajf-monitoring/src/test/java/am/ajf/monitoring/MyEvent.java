package am.ajf.monitoring;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import am.ajf.monitoring.AbstractEvent;

@XmlRootElement
public class MyEvent extends AbstractEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String uuid;
	private String firstName;
	private String lastName;

	/**
	 * 
	 */
	public MyEvent() {
		super();
		this.uuid = UUID.randomUUID().toString();
	}
	
	/**
	 * @param firstName
	 * @param lastName
	 */
	public MyEvent(String firstName, String lastName) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
}
