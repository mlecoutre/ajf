package ajf.monitoring;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import ajf.monitoring.AbstractEvent;

@XmlRootElement
public class MyEvent extends AbstractEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String firstName;
	private String lastName;

	/**
	 * 
	 */
	public MyEvent() {
		super(UUID.randomUUID().toString());
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

	/**
	 * @param firstName
	 * @param lastName
	 */
	public MyEvent(String firstName, String lastName) {
		super(UUID.randomUUID().toString());
		this.firstName = firstName;
		this.lastName = lastName;
	}

}
