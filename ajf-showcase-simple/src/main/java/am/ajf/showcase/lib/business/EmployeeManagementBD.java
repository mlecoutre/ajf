package am.ajf.showcase.lib.business;

import ajf.services.exceptions.BusinessLayerException;
import am.ajf.showcase.lib.dto.ListEmployeesPB;
import am.ajf.showcase.lib.dto.ListEmployeesRB;

public interface EmployeeManagementBD {

	public ListEmployeesRB listEmployees(ListEmployeesPB employeesPB)
			throws BusinessLayerException;

}
