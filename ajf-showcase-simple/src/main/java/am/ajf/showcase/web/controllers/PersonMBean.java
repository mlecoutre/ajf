package am.ajf.showcase.web.controllers;

import static ajf.web.WebUtils.handleError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.slf4j.Logger;

import ajf.injection.InjectionContext;
import ajf.logger.injection.InjectLogger;
import ajf.services.injection.InjectService;
import am.ajf.showcase.lib.business.EmployeeManagementBD;
import am.ajf.showcase.lib.dto.ListEmployeesPB;
import am.ajf.showcase.lib.dto.ListEmployeesRB;
import am.ajf.showcase.lib.model.Person;

@ManagedBean
@ViewScoped
public class PersonMBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/* ATTRIBUTES */
	private String firstname;

	@Size(min = 1, max = 32)
	@NotNull
	private String lastname;

	private String sex;
	private Date birthday;
	private List<Person> employees = new ArrayList<Person>();

	/* POLICIES BUSINESS DELEGATE */
	/*
	 * will be replaced by @Inject in AJF 2.0.2
	 */
	@InjectService
	EmployeeManagementBD employeeManagement;

	/* CONSTANTS */
	//private int MAX_RESULT = 200;

	/*
	 * will be replaced by @Inject in AJF 2.0.2
	 */
	@InjectLogger
	private Logger logger;

	public PersonMBean() {

	}

	@PostConstruct
	public void init() {
		/*
		 * @TODO will be removed in @Inject in AJF 2.0.2
		 */
		InjectionContext.getInstance().getInjector().injectMembers(this);
	}

	/**
	 * DEFAULT Constructor
	 */
	public void createEmployee() {

	}

	public void listEmployees() {
		logger.debug("listEmployees MBean");
		try {
			// Initialize parambean
			ListEmployeesPB pb = new ListEmployeesPB(lastname);

			// Call business layer
			ListEmployeesRB rb = employeeManagement.listEmployees(pb);

			// Get Result
			employees = rb.getEmployees();

		} catch (Exception e) {
			// import static ajf.web.WebUtils.*;
			handleError(e, logger, "Error when try to list employees");
		}
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
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

}
