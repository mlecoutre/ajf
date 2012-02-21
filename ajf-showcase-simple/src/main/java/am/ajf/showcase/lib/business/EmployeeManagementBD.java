package am.ajf.showcase.lib.business;

import am.ajf.core.services.exceptions.BusinessLayerException;
import am.ajf.showcase.lib.business.dto.FireEmployeePB;
import am.ajf.showcase.lib.business.dto.FireEmployeeRB;
import am.ajf.showcase.lib.business.dto.HireEmployeePB;
import am.ajf.showcase.lib.business.dto.HireEmployeeRB;
import am.ajf.showcase.lib.business.dto.ListEmployeesPB;
import am.ajf.showcase.lib.business.dto.ListEmployeesRB;

/**
 * Business Policy contract
 * 
 * @author E010925
 * 
 */
public interface EmployeeManagementBD {

	public ListEmployeesRB listEmployees(ListEmployeesPB employeesPB)
			throws BusinessLayerException;

	public HireEmployeeRB hireEmployee(HireEmployeePB employeeePB)
			throws BusinessLayerException;

	public FireEmployeeRB fireEmployee(FireEmployeePB employeePB)
			throws BusinessLayerException;

}
