package am.ajf.showcase.lib.business.dto;

import am.ajf.showcase.lib.model.Person;

/**
 * Fire employee parameter
 * 
 * @author E010925
 * 
 */
public class FireEmployeePB {

	private Person person;

	public FireEmployeePB() {
		super();
	}

	public FireEmployeePB(Person person) {
		super();
		this.person = person;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}
