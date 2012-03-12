package am.ajf.showcase.web.controllers;

import am.ajf.core.logger.LoggerFactory;
import am.ajf.showcase.lib.business.EmployeeManagementBD;
import am.ajf.showcase.lib.business.dto.FireEmployeePB;
import am.ajf.showcase.lib.business.dto.FireEmployeeRB;
import am.ajf.showcase.lib.business.dto.HireEmployeePB;
import am.ajf.showcase.lib.business.dto.HireEmployeeRB;
import am.ajf.showcase.lib.business.dto.ListEmployeesPB;
import am.ajf.showcase.lib.business.dto.ListEmployeesRB;
import am.ajf.showcase.lib.model.Person;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.slf4j.Logger;

/**
 * Managed bean to get input of client. This class has to implement
 * <i>serializable</i> when we use View or Session scope.
 * 
 * @author E010925
 * 
 */
@Named
@SessionScoped
public class EmployeeMBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient Logger logger = LoggerFactory
			.getLogger(EmployeeMBean.class);

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
	protected EmployeeManagementBD employeeManagement;

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
	 * 
	 * @throws Exception
	 *             raise all error
	 */
	public void hireEmployee() throws Exception {
		logger.debug("createEmployee MBean");

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
			FacesContext.getCurrentInstance().addMessage(null, facesMessage);
		}

	}

	/**
	 * Remove an employee in the database
	 * 
	 * @throws Exception
	 *             raise all error
	 */
	public void fireEmployee() throws Exception {
		logger.debug("fireEmployee MBean");

		FireEmployeePB pb = new FireEmployeePB(selectedEmployee);

		// Call business layer
		FireEmployeeRB rb = employeeManagement.fireEmployee(pb);

		if (rb.isRemoved()) {
			// remove in the local list;another choice could be to request
			// the DB
			this.employees.remove(selectedEmployee);
			FacesMessage facesMessage = new FacesMessage(
					FacesMessage.SEVERITY_INFO, "Employee fired!", "");
			FacesContext.getCurrentInstance().addMessage(null, facesMessage);
		}

	}

	/**
	 * list employee
	 * 
	 * @throws Exception
	 *             raise all error
	 */
	public void listEmployees() throws Exception {

		logger.debug("listEmployees MBean");

		ListEmployeesPB pb = new ListEmployeesPB(searchLastname);

		// Call business layer
		ListEmployeesRB rb = employeeManagement.listEmployees(pb);

		// Get Result
		employees = rb.getEmployees();
	}

	/**
	 * 
	 * @return sex
	 */
	public char getSex() {
		return sex;
	}

	/**
	 * 
	 * @param sex
	 *            person sec
	 */
	public void setSex(char sex) {
		this.sex = sex;
	}

	/**
	 * 
	 * @return brithday date
	 */
	public Date getBirthday() {
		return birthday;
	}

	/**
	 * 
	 * @param birthday
	 *            birthday date
	 */
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
