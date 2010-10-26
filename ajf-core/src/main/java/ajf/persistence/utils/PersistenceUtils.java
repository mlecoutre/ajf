package ajf.persistence.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

import org.slf4j.Logger;

import ajf.persistence.exception.PersistenceLayerException;

/**
 * Utility class for persistence layer
 * 
 * @author E010925
 * 
 */
public class PersistenceUtils {

	/**
	 * Opens the entity manager.
	 * 
	 * @param persistenceUnit
	 * @return EntityManager
	 */
	public EntityManager openEntityManager(String persistenceUnit) {
		EntityManagerFactory factory = Persistence
				.createEntityManagerFactory(persistenceUnit);
		EntityManager em = null;
		if (em == null) {
			em = factory.createEntityManager();
			em.setFlushMode(FlushModeType.AUTO);
		}
		if (!em.isOpen()) {
			em = factory.createEntityManager();
			em.setFlushMode(FlushModeType.AUTO);
		}
		return em;
	}

	/**
	 * Closes the entity manager
	 * 
	 * @param em
	 *            EntityManager
	 */
	public void closeEntityManager(EntityManager em) {
		em.close();
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
		String errorMsg = new StringBuffer("Managed persistence error: ")
				.append(cause.getMessage()).append(" ")
				.append(cause.getCause().getMessage()).toString();
		log.error(errorMsg, cause);
		throw new PersistenceLayerException(errorMsg, errorType, null);
	}

}
