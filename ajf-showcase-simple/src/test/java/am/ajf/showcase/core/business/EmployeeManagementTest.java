package am.ajf.showcase.core.business;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;

import ajf.logger.injection.InjectLogger;
import ajf.services.exceptions.BusinessLayerException;
import ajf.services.injection.InjectService;
import ajf.testing.junit.DITestRunnner;
import am.ajf.showcase.core.services.PersonService;
import am.ajf.showcase.lib.business.EmployeeManagementBD;
import am.ajf.showcase.lib.business.dto.ListEmployeesPB;
import am.ajf.showcase.lib.business.dto.ListEmployeesRB;
import am.ajf.showcase.lib.model.Person;
import am.ajf.showcase.lib.services.PersonServiceBD;

/**
 * Singleton
 * 
 * @author E010925
 * 
 */
@RunWith(DITestRunnner.class)
public class EmployeeManagementTest {

	@InjectLogger
	private Logger logger;

	@InjectService
	EmployeeManagementBD employeeManagement;

	PersonServiceBD personService;

	/**
	 * DEFAULT CONSTRUCTOR
	 */
	public EmployeeManagementTest() {
		super();
	}

	@org.junit.Before
	public void setup() {
		personService = Mockito.mock(PersonService.class);

	}

	@Test
	public void testListEmployees() throws BusinessLayerException {
		logger.debug("testListEmployees");

		List<Person> persons = new ArrayList<Person>();
		Person p = new Person();
		p.setFirstname("firstname");
		p.setLastname("lastname");
		persons.add(p);

		ListEmployeesPB employeesPB = new ListEmployeesPB("%");
		Mockito.when(personService.findByLastname("%")).thenReturn(persons);
		Mockito.when(personService.findByLastname("Exception")).thenThrow(
				new BusinessLayerException("errorMsg"));
		((EmployeeManagementPolicy) employeeManagement).personServiceBD = personService;

		ListEmployeesRB rb = employeeManagement.listEmployees(employeesPB);
		assertTrue("We should have one person in the list", rb.getEmployees()
				.size() == 1);

		ListEmployeesPB employeesPBEx = new ListEmployeesPB("Exception");
		boolean isBle = false;
		try {
			rb = employeeManagement.listEmployees(employeesPBEx);
		} catch (BusinessLayerException ble) {
			isBle = true;
		}
		assertTrue("We should have raised a BusinessLayerException", isBle);

	}

}
