package ajf.persistence.utils;

import org.slf4j.Logger;

import ajf.persistence.exception.PersistenceLayerException;

/**
 * 
 * @author E010925
 * 
 */
public class PersistenceUtils {

	/**
	 * manage persistence layer exception
	 * 
	 * @param log
	 *            class logger
	 * @param message
	 *            error message
	 * @param cause
	 *            inner exception
	 * @throws PersistenceLayerException
	 */
	public static void handlerError(Logger log, String message, Throwable cause)
			throws PersistenceLayerException {
		String errorMsg = new StringBuffer("Managed persistence error: ")
				.append(cause.getMessage()).append(" ")
				.append(cause.getCause().getMessage()).toString();
		log.error(errorMsg, cause);
		throw new PersistenceLayerException(errorMsg);
	}

	/**
	 * manage persistence layer exception
	 * 
	 * @param log
	 *            class logger
	 * @param message
	 * 
	 * @param errorType
	 *            type of error (free for developer)
	 * @param cause
	 *            inner exception
	 * @throws PersistenceLayerException
	 */
	public static void handlerError(Logger log, String message,
			String errorType, Throwable cause) throws PersistenceLayerException {
		String errorMsg = new StringBuffer("Managed persistence error: ")
				.append(cause.getMessage()).append(" ")
				.append(cause.getCause().getMessage()).toString();
		log.error(errorMsg, cause);
		throw new PersistenceLayerException(errorMsg);
	}

}
