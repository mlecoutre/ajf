package am.ajf.showcase.lib.dto;

import java.util.List;

import am.ajf.showcase.lib.model.Person;

/**
 * Contain Result of the policy Function P+: Empl
 * 
 * @author E010925
 * 
 */
public class ListEmployeesRB {

	private List<Person> employees;

	public ListEmployeesRB() {

	}

	public ListEmployeesRB(List<Person> employees) {
		this.employees = employees;
	}

	public List<Person> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Person> employees) {
		this.employees = employees;
	}

}
