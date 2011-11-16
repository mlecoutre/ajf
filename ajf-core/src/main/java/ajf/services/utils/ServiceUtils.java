package ajf.services.utils;

import org.slf4j.Logger;

import ajf.services.exceptions.ServiceLayerException;

/**
 * Utility class for persistence layer
 * 
 * @author E010925
 * 
 */
public class ServiceUtils {
	
	private ServiceUtils() {
		super();
	}

	/**
	 * manage service layer exception
	 * 
	 * @param log
	 *            class logger
	 * @param message
	 *            error message
	 * @param cause
	 *            inner exception
	 * @throws ServiceLayerException
	 */
	public static void handlerError(Logger log, String message, Throwable cause)
			throws ServiceLayerException {
		handlerError(log, message, "", cause);
	}

	/**
	 * manage service layer exception. It's raised within Persistence layer to
	 * the Business Layer. Not sent to the presentation Layer
	 * 
	 * @param log
	 *            class logger
	 * @param message
	 * 
	 * @param errorType
	 *            type of error (free for developer)
	 * @param cause
	 *            inner exception
	 * @throws ServiceLayerException
	 */
	public static void handlerError(Logger log, String message,
			String errorType, Throwable cause) throws ServiceLayerException {
		
		StringBuffer buffer = new StringBuffer("Managed service error : ")
				.append(cause.getMessage()).append(" ");
		if (cause.getCause() != null) {
			buffer.append(cause.getCause().getMessage());
		}
		String errorMsg = buffer.toString();
		log.error(errorMsg, cause);
		
		Throwable propagatedCause = cause;
		/*
		if (cause != null) {
			if (cause instanceof PersistenceLayerException) {
				PersistenceLayerException pLayerException = (PersistenceLayerException) cause;
				propagatedCause = new PersistenceLayerException(pLayerException.getMessage(),
						pLayerException.getErrorType(), null);
			}
		}
		*/		
		throw new ServiceLayerException(errorMsg, errorType, propagatedCause);
	}

}
