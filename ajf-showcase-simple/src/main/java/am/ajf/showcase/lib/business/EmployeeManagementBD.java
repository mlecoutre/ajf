package am.ajf.showcase.lib.business;

import ajf.services.exceptions.BusinessLayerException;
import am.ajf.showcase.lib.business.dto.ListEmployeesPB;
import am.ajf.showcase.lib.business.dto.ListEmployeesRB;

public interface EmployeeManagementBD {

	public ListEmployeesRB listEmployees(ListEmployeesPB employeesPB)
			throws BusinessLayerException;

}
