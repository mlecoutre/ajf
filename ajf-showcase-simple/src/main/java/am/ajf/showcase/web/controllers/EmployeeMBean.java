package am.ajf.showcase.web.controllers;

import static am.ajf.web.WebUtils.handleError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.slf4j.Logger;

import am.ajf.showcase.lib.business.EmployeeManagementBD;
import am.ajf.showcase.lib.business.dto.FireEmployeePB;
import am.ajf.showcase.lib.business.dto.FireEmployeeRB;
import am.ajf.showcase.lib.business.dto.HireEmployeePB;
import am.ajf.showcase.lib.business.dto.HireEmployeeRB;
import am.ajf.showcase.lib.business.dto.ListEmployeesPB;
import am.ajf.showcase.lib.business.dto.ListEmployeesRB;
import am.ajf.showcase.lib.model.Person;

/**
 * Managed bean to get input of client. This class has to implement
 * <i>serializable</i> when we use View or Session scope.
 * 
 * @author E010925
 * 
 */
@Named
@RequestScoped
public class EmployeeMBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	/* ATTRIBUTES */

	@Size(min = 4, max = 32)
	@NotNull
	private String lastname;
	@Size(min = 4, max = 32)
	@NotNull
	private String firstname;
	private char sex;
	private Date birthday;
	private List<Person> employees = new ArrayList<Person>();
	private Person selectedEmployee;

	@Size(min = 1, max = 32)
	@NotNull
	private String searchLastname;

	/* POLICIES BUSINESS DELEGATE */
	
	@Inject
	EmployeeManagementBD employeeManagement;

	/* CONSTANTS */
	// private int MAX_RESULT = 200;

	/**
	 * DEFAULT Constructor
	 */
	public EmployeeMBean() {
		super();
	}

	/**
	 * Create an employee in the database
	 */
	public void hireEmployee() {
		logger.debug("createEmployee MBean");

		try {
			Person p = new Person();
			p.setFirstname(firstname);
			p.setLastname(lastname);
			p.setBirthday(birthday);
			p.setSex(sex);

			HireEmployeePB pb = new HireEmployeePB(p);

			// Call business layer
			HireEmployeeRB rb = employeeManagement.hireEmployee(pb);

			if (rb.isHired()) {
				FacesMessage facesMessage = new FacesMessage(
						FacesMessage.SEVERITY_INFO, "Employee created", "");
				FacesContext.getCurrentInstance()
						.addMessage(null, facesMessage);
			}
		} catch (Exception e) {
			// import static am.ajf.web.WebUtils.*;
			handleError(e, logger, "Error when try to list employees");
		}

	}

	/**
	 * Remove an employee in the database
	 */
	public void fireEmployee() {
		logger.debug("fireEmployee MBean");

		try {

			FireEmployeePB pb = new FireEmployeePB(selectedEmployee);

			// Call business layer
			FireEmployeeRB rb = employeeManagement.fireEmployee(pb);

			if (rb.isRemoved()) {
				//remove in the local list;another choice could be to request the DB
				this.employees.remove(selectedEmployee);
				FacesMessage facesMessage = new FacesMessage(
						FacesMessage.SEVERITY_INFO, "Employee fired!", "");
				FacesContext.getCurrentInstance()
						.addMessage(null, facesMessage);
			}
		} catch (Exception e) {
			// import static ajf.web.WebUtils.*;
			handleError(e, logger, "Error when try to list employees");
		}
	}

	/**
	 * list employee
	 */
	public void listEmployees() {

		logger.debug("listEmployees MBean");
		try {

			ListEmployeesPB pb = new ListEmployeesPB(searchLastname);

			// Call business layer
			ListEmployeesRB rb = employeeManagement.listEmployees(pb);

			// Get Result
			employees = rb.getEmployees();

		} catch (Exception e) {
			// import static ajf.web.WebUtils.*;
			handleError(e, logger, "Error when try to list employees");
		}
	}

	public char getSex() {
		return sex;
	}

	public void setSex(char sex) {
		this.sex = sex;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public List<Person> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Person> employees) {
		this.employees = employees;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Person getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(Person selectedPerson) {
		this.selectedEmployee = selectedPerson;
	}

	public String getSearchLastname() {
		return searchLastname;
	}

	public void setSearchLastname(String searchLastname) {
		this.searchLastname = searchLastname;
	}

}
