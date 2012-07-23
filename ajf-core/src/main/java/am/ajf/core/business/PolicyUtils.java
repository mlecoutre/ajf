package am.ajf.core.business;

import org.slf4j.Logger;

import am.ajf.core.exceptions.AbstractLayerException;
import am.ajf.core.services.exceptions.BusinessLayerException;

/**
 * Utility class for Policies
 * @author E010925
 *
 */
public class PolicyUtils {

	/**
	 * Manage Default ErrorHandling for Policy.
	 * This class is also use in @link{ErrorHandlingInterceptor}
	 * <ul>
	 * <li>Check if AbstractLayerException</li>
	 * <li>Check already trace and trace it if needed</li>
	 * <li>Create a Business Layer Exception (if not a ble) and put in cause the
	 * exception</li>
	 * <li>Throw the BusinessLayerException</li>
	 * </ul>
	 * 
	 * @param exception
	 *            Exception thrown by the policy class
	 * @param targetClass
	 *            class where error has occurred
	 * @param targetLog
	 *            logger of the targeted class
	 * @throws BusinessLayerException
	 *             throws only Business Layer Exception
	 */
	public static void handleError(Exception exception,
			Class<? extends Object> targetClass, Logger targetLog)
			throws BusinessLayerException {
		String msg = "@ErrorHandlingInterceptor on Policy";
		BusinessLayerException ble = null;

		if (exception instanceof AbstractLayerException) {
			AbstractLayerException ale = (AbstractLayerException) exception;

			if (!ale.isAlreadyHandled()) {
				targetLog.error(msg, exception);
			}
			if (!(ale instanceof BusinessLayerException)) {
				ale = new BusinessLayerException(ale);
			}
			ble = (BusinessLayerException) ale;
		} else {
			targetLog.error(msg, exception);
			ble = new BusinessLayerException(msg, exception);

		}
		ble.setAlreadyHandled(true);
		throw ble;

	}
}
