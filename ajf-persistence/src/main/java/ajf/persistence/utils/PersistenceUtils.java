package ajf.persistence.utils;

import javax.persistence.EntityManager;

import org.slf4j.Logger;

import ajf.persistence.EntityManagerProvider;
import ajf.persistence.exception.PersistenceLayerException;

/**
 * Utility class for persistence layer
 * 
 * @author E010925
 * 
 */
public class PersistenceUtils {
	
	private PersistenceUtils() {
		super();
	}

	/**
	 * Opens the entity manager.
	 * 
	 * @param persistenceUnit
	 * @return EntityManager
	 */
	public static EntityManager openEntityManager(String persistenceUnit) {
		return EntityManagerProvider.getEntityManager(persistenceUnit);
	}

	/**
	 * Closes the entity manager
	 * 
	 * @param em
	 *            EntityManager
	 */
	public static void closeEntityManager(EntityManager em) {
		if ((em != null) && (em.isOpen()))
				em.close();
		EntityManagerProvider.clean();
	}

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
		handlerError(log, message, "", cause);
	}

	/**
	 * manage persistence layer exception. It's raised within Persistence layer
	 * to the Business Layer. Not sent to the presentation Layer
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
		
		StringBuffer buffer = new StringBuffer("Managed persistence error : ")
				.append(cause.getMessage()).append(" ");
		if (cause.getCause() != null) {
			buffer.append(cause.getCause().getMessage());
		}
		String errorMsg = buffer.toString();
		log.error(errorMsg, cause);
		throw new PersistenceLayerException(errorMsg, errorType, null);
	}

}