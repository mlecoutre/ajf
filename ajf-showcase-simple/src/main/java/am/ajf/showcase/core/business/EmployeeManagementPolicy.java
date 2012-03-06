package am.ajf.showcase.core.business;

import am.ajf.showcase.lib.business.EmployeeManagementBD;
import am.ajf.showcase.lib.business.dto.FireEmployeePB;
import am.ajf.showcase.lib.business.dto.FireEmployeeRB;
import am.ajf.showcase.lib.business.dto.HireEmployeePB;
import am.ajf.showcase.lib.business.dto.HireEmployeeRB;
import am.ajf.showcase.lib.business.dto.ListEmployeesPB;
import am.ajf.showcase.lib.business.dto.ListEmployeesRB;
import am.ajf.showcase.lib.model.Person;
import am.ajf.showcase.lib.services.PersonServiceBD;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 * Singleton implementation of EmployeeManagement Palas Function
 * 
 * @author E010925
 * 
 */
@ApplicationScoped
public class EmployeeManagementPolicy implements EmployeeManagementBD {

	@Inject
	PersonServiceBD personServiceBD;

	@Inject
	Logger logger;

	/**
	 * DEFAULT CONSTRUCTOR
	 */
	public EmployeeManagementPolicy() {
		super();
	}

	@Override
	public ListEmployeesRB listEmployees(ListEmployeesPB pb) throws Exception {
		logger.debug("listEmployees policy");
		ListEmployeesRB rb = new ListEmployeesRB();

		List<Person> persons = personServiceBD.findByLastname(pb.getLastname());
		rb.setEmployees(persons);

		return rb;
	}

	@Override
	public HireEmployeeRB hireEmployee(HireEmployeePB employeeePB)
			throws Exception {
		logger.debug("hireEmployee policy");
		HireEmployeeRB rb = new HireEmployeeRB();

		boolean isHired = personServiceBD.create(employeeePB.getPerson());
		rb.setHired(isHired);

		return rb;
	}

	@Override
	public FireEmployeeRB fireEmployee(FireEmployeePB employeePB)
			throws Exception {
		logger.debug("fireEmployee policy");
		FireEmployeeRB rb = new FireEmployeeRB();

		boolean isFired = personServiceBD.removeByPrimaryKey(employeePB
				.getPerson().getPersonid());
		rb.setRemoved(isFired);

		return rb;
	}

}
