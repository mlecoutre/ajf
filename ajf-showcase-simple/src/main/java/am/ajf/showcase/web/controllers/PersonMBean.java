package am.ajf.showcase.web.controllers;

import static ajf.web.WebUtils.handleError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.slf4j.Logger;

import ajf.injection.InjectionContext;
import ajf.logger.injection.InjectLogger;
import ajf.services.injection.InjectService;
import am.ajf.showcase.lib.business.EmployeeManagementBD;
import am.ajf.showcase.lib.business.dto.ListEmployeesPB;
import am.ajf.showcase.lib.business.dto.ListEmployeesRB;
import am.ajf.showcase.lib.model.Person;

@ManagedBean
@ViewScoped
public class PersonMBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public interface EditEmployeeCheck {
		
	}
	
	/*
	 * will be replaced by @Inject in AJF 2.0.2
	 */
	@InjectLogger
	private Logger logger;

	/* ATTRIBUTES */
	private String firstname;

	@Size(groups= EditEmployeeCheck.class, min = 2, max = 32)
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

	/**
	 * DEFAULT Constructor
	 */
	public PersonMBean() {
		super();
	}

	@PostConstruct
	public void init() {
		/*
		 * @TODO will be removed in @Inject in AJF 2.0.2
		 */
		InjectionContext.getInstance().getInjector().injectMembers(this);
	}

	public void createEmployee() {

	}

	public void listEmployees() {
		
		/*
		DefaultAuditData auditData = new DefaultAuditData();
		auditData.put(AuditData.KEY_USERID, "User");
		AuditDataContext.initContextData(auditData);
		*/
		
		logger.debug("listEmployees MBean");
		try {
			
			Validator validator = getValidator();
			Set<ConstraintViolation<PersonMBean>> constraintViolations = validator.validate(this, EditEmployeeCheck.class);
			
			if (constraintViolations.isEmpty()) {
				// Initialize parambean
				ListEmployeesPB pb = new ListEmployeesPB(lastname);

				// Call business layer
				ListEmployeesRB rb = employeeManagement.listEmployees(pb);

				// Get Result
				employees = rb.getEmployees();
			}
			else {
				/*
				FacesMessage facesMessage = new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						WebUtils.getFieldLabel("application.error.occured"), message);
				FacesContext.getCurrentInstance().addMessage(clientId, facesMessage);
				*/
			}
			
		} catch (Exception e) {
			// import static ajf.web.WebUtils.*;
			handleError(e, logger, "Error when try to list employees");
		}
	}

	private Validator getValidator() {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		Validator validator = validatorFactory.getValidator();
		return validator;
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
