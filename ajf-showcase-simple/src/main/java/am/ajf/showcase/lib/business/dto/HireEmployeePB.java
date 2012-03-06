package am.ajf.showcase.lib.business.dto;

import am.ajf.showcase.lib.model.Person;

/**
 * HireEmployeePB parameter
 * 
 * @author E010925
 * 
 */
public class HireEmployeePB {

	private Person person;

	public HireEmployeePB() {
		super();
	}

	public HireEmployeePB(Person person) {
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
