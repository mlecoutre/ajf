package am.ajf.showcase.core.business;

import java.util.List;

import javax.inject.Singleton;

import org.slf4j.Logger;

import ajf.logger.injection.InjectLogger;
import ajf.services.exceptions.BusinessLayerException;
import ajf.services.injection.InjectService;
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
@Singleton
public class EmployeeManagementPolicy implements EmployeeManagementBD {

	@InjectService
	PersonServiceBD personServiceBD;

	@InjectLogger
	private Logger logger;

	/**
	 * DEFAULT CONSTRUCTOR
	 */
	public EmployeeManagementPolicy() {
		super();
	}

	@Override
	public ListEmployeesRB listEmployees(ListEmployeesPB pb)
			throws BusinessLayerException {
		logger.debug("listEmployees policy");
		ListEmployeesRB rb = new ListEmployeesRB();
		try {

			List<Person> persons = personServiceBD.findByLastname(pb
					.getLastname());
			rb.setEmployees(persons);

		} catch (Throwable th) {

			handleError("listEmployees", th);
		}
		return rb;
	}

	/**
	 * Manage Errors coming from other layers TODO put this method in the
	 * framework
	 * 
	 * @param msg
	 *            custom message
	 * @param th
	 *            Throwable to manage
	 * 
	 */
	private void handleError(String msg, Throwable th)
			throws BusinessLayerException {
		BusinessLayerException ble = null;
		if (th instanceof BusinessLayerException) {
			ble = (BusinessLayerException) th;
			if (!ble.isAlreadyHandled()) {
				logger.error(msg, th);
				ble.setAlreadyHandled(true);
			}
		} else {
			logger.error(msg, th);
			ble = new BusinessLayerException(msg, th);
		}
		throw ble;
	}
}
