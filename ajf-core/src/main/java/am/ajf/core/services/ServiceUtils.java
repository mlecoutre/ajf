package am.ajf.core.services;

import org.slf4j.Logger;

import am.ajf.core.exceptions.AbstractLayerException;
import am.ajf.core.services.exceptions.ServiceLayerException;

/**
 * Utility class for Services
 * 
 * @author E010925
 * 
 */
public class ServiceUtils {

	/**
	 * Manage Default ErrorHandling for Service.
	 * <ul>
	 * <li>Check if AbstractLayerException</li>
	 * <li>Check already trace and trace it if needed</li>
	 * <li>Create a Business Layer Exception (if not a ble) and put in cause the
	 * exception</li>
	 * <li>Throw the ServiceLayerException</li>
	 * </ul>
	 * 
	 * @param exception
	 *            Exception thrown by the Server class
	 * @param targetClass
	 *            class where error has occurred
	 * @param targetLog
	 *            logger of the targeted class
	 * @throws ServiceLayerException
	 *             throws only Server Layer Exception
	 */
	public static void handleError(Exception exception,
			Class<? extends Object> targetClass, Logger targetLog)
			throws ServiceLayerException {
		String msg = "@ErrorHandlingInterceptor on Service";
		ServiceLayerException ble = null;

		if (exception instanceof AbstractLayerException) {
			AbstractLayerException ale = (AbstractLayerException) exception;

			if (!ale.isAlreadyHandled()) {
				targetLog.error(msg, exception);
			}
			if (!(ale instanceof ServiceLayerException)) {
				ble = new ServiceLayerException(ale);
			}
			ble = (ServiceLayerException) ale;
		} else {
			targetLog.error(msg, exception);
			ble = new ServiceLayerException(msg, exception);

		}
		ble.setAlreadyHandled(true);
		throw ble;

	}
}
