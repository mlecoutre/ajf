package am.ajf.showcase.lib.business;

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
			throws Exception;

	public HireEmployeeRB hireEmployee(HireEmployeePB employeeePB)
			throws Exception;

	public FireEmployeeRB fireEmployee(FireEmployeePB employeePB)
			throws Exception;

}
