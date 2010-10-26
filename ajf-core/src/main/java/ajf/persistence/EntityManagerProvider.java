package ajf.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

public abstract class EntityManagerProvider {

	private static ThreadLocal<Map<String, EntityManager>> emMaps = new ThreadLocal<Map<String,EntityManager>>();

	/**
	 * set a new entity manager
	 * @param persistenceUnitName
	 * @param entityManager
	 */
	public static void setEntityManager(String persistenceUnitName, EntityManager entityManager) {
		
		Map<String, EntityManager> entityManagersMap = getEntityManagersMap();
		if (null == entityManager) {
			if (entityManagersMap.containsKey(persistenceUnitName))
				entityManagersMap.remove(persistenceUnitName);
			return;
		}
		entityManagersMap.put(persistenceUnitName, entityManager);
	}

	/**
	 * 
	 * @return
	 */
	private static Map<String, EntityManager> getEntityManagersMap() {
		Map<String,EntityManager> entityManagersMap = emMaps.get();
		if (null == entityManagersMap) {
			entityManagersMap = new HashMap<String, EntityManager>();
			emMaps.set(entityManagersMap);
		}
		return entityManagersMap;
	}
	
	/**
	 * get entityManager
	 * @param persistenceUnitName
	 * @return
	 */
	public static EntityManager getEntityManager(String persistenceUnitName) {
		
		Map<String, EntityManager> entityManagersMap = getEntityManagersMap();
		
		EntityManager em = entityManagersMap.get(persistenceUnitName);
		if ((null == em) || (!em.isOpen())) {
			EntityManagerFactory emFactory = Persistence
				.createEntityManagerFactory(persistenceUnitName);
			em = emFactory.createEntityManager();
			// remove auto-update
			em.setFlushMode(FlushModeType.COMMIT);
			// register the new entity manager
			setEntityManager(persistenceUnitName, em);
		}
		return em;
	}
	
	/**
	 * close the entityManager
	 * @param persistenceUnitName
	 */
	public static void close(String persistenceUnitName) {
		
		Map<String, EntityManager> entityManagersMap = getEntityManagersMap();
		EntityManager em = entityManagersMap.get(persistenceUnitName);
		if (null != em) {
			if (em.isOpen())
				em.close();
			em = null;
			// register the new entity manager
			setEntityManager(persistenceUnitName, null);
		}
	}
	
	/**
	 * close all the the entityManagers
	 */
	public static void closeAll() {
		
		Map<String, EntityManager> entityManagersMap = getEntityManagersMap();
		for (String persistenceUnitName : entityManagersMap.keySet()) {
			EntityManager em = entityManagersMap.get(persistenceUnitName);
			if (null != em) {
				if (em.isOpen())
					em.close();
				em = null;
				// register the new entity manager
				setEntityManager(persistenceUnitName, null);
			}
		}
		
	}

	/**
	 * remove all closed entityManagers
	 */
	public static void clean() {
		
		Map<String, EntityManager> entityManagersMap = getEntityManagersMap();
		for (String persistenceUnitName : entityManagersMap.keySet()) {
			EntityManager em = entityManagersMap.get(persistenceUnitName);
			if ((null != em) && (!em.isOpen())) {
				setEntityManager(persistenceUnitName, null);
			}
		}
		
	}
	
}
