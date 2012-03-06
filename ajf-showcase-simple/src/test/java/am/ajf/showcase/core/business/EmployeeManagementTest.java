package am.ajf.showcase.core.business;

import static org.junit.Assert.assertTrue;

import am.ajf.core.logger.LoggerFactory;
import am.ajf.core.services.exceptions.ServiceLayerException;
import am.ajf.showcase.core.services.PersonService;
import am.ajf.showcase.lib.business.dto.FireEmployeePB;
import am.ajf.showcase.lib.business.dto.FireEmployeeRB;
import am.ajf.showcase.lib.business.dto.HireEmployeePB;
import am.ajf.showcase.lib.business.dto.HireEmployeeRB;
import am.ajf.showcase.lib.business.dto.ListEmployeesPB;
import am.ajf.showcase.lib.business.dto.ListEmployeesRB;
import am.ajf.showcase.lib.model.Person;
import am.ajf.showcase.lib.services.PersonServiceBD;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

/**
 * EmployeeManagementTest
 * 
 * @author E010925
 * 
 */
public class EmployeeManagementTest {

	private final EmployeeManagementPolicy employeeManagement = new EmployeeManagementPolicy();

	private final Logger logger = LoggerFactory
			.getLogger(EmployeeManagementTest.class);

	private PersonServiceBD personService;

	/**
	 * DEFAULT CONSTRUCTOR
	 */
	public EmployeeManagementTest() {
		super();
	}

	/**
	 * init person service
	 */
	@org.junit.Before
	public void setup() {
		personService = Mockito.mock(PersonService.class);

	}

	/**
	 * test fire employee
	 * 
	 * @throws Exception
	 *             on error
	 */
	@Test
	public void testFireEmployee() throws Exception {
		Mockito.when(personService.removeByPrimaryKey(Mockito.anyLong()))
				.thenReturn(true);
		employeeManagement.personServiceBD = personService;
		employeeManagement.logger = logger;
		Person p = new Person();
		p.setPersonid(1l);
		FireEmployeePB pb = new FireEmployeePB(p);
		FireEmployeeRB rb = employeeManagement.fireEmployee(pb);
		assertTrue("Employee should be fired", rb.isRemoved());
	}

	/**
	 * test on hire employee
	 * 
	 * @throws Exception
	 *             on error
	 */
	@Test
	public void testHireEmployee() throws Exception {
		Mockito.when(personService.create(Mockito.any(Person.class)))
				.thenReturn(true);
		employeeManagement.personServiceBD = personService;
		employeeManagement.logger = logger;
		Person p = new Person();
		p.setPersonid(1l);
		HireEmployeePB pb = new HireEmployeePB();
		HireEmployeeRB rb = employeeManagement.hireEmployee(pb);
		assertTrue("Employee should be hired", rb.isHired());
	}

	@Test
	public void testListEmployees() throws Exception {
		logger.debug("testListEmployees");

		List<Person> persons = new ArrayList<Person>();
		Person p = new Person();
		p.setFirstname("firstname");
		p.setLastname("lastname");
		persons.add(p);

		ListEmployeesPB employeesPB = new ListEmployeesPB("%");
		Mockito.when(personService.findByLastname("%")).thenReturn(persons);

		employeeManagement.personServiceBD = personService;
		employeeManagement.logger = logger;

		ListEmployeesRB rb = employeeManagement.listEmployees(employeesPB);
		assertTrue("We should have one person in the list", rb.getEmployees()
				.size() == 1);

	}

	/*
	 * In Unit tests, interceptor are not use so it means that any exception go
	 * through the different layer without any transformation. If you need
	 * arquillian to activate CDI for policies,it means that u do Integration
	 * Tests.
	 */
	@Test(expected = ServiceLayerException.class)
	public void testListEmployeesWhenError() throws Exception {
		Mockito.when(personService.findByLastname(Mockito.anyString()))
				.thenThrow(new ServiceLayerException("errorMsg"));
		ListEmployeesPB employeesPBEx = new ListEmployeesPB("Exception");
		employeeManagement.personServiceBD = personService;
		employeeManagement.logger = logger;

		employeeManagement.listEmployees(employeesPBEx);

	}

}
