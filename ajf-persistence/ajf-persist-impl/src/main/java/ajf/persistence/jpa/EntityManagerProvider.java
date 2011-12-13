package ajf.persistence.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public abstract class EntityManagerProvider {

	private static Object emfToken = new Object();
	private static Map<String, EntityManagerFactory> emfMap = new HashMap<String, EntityManagerFactory>();
	private static ThreadLocal<Map<String, EntityManager>> emMaps = new ThreadLocal<Map<String, EntityManager>>();

	/**
	 * set a new entity manager
	 * 
	 * @param persistenceUnitName
	 * @param entityManager
	 */
	public static void setEntityManager(String persistenceUnitName,
			EntityManager entityManager) {

		Map<String, EntityManager> entityManagersMap = getEntityManagersMap();
		if (null == entityManager) {
			if (entityManagersMap.containsKey(persistenceUnitName))
				entityManagersMap.remove(persistenceUnitName);
			return;
		}
		entityManagersMap.put(persistenceUnitName, entityManager);
	}

	/**
	 * set a new entity manager factory
	 * 
	 * @param persistenceUnitName
	 * @param entityManagerFactory
	 */
	public static void setEntityManagerFactory(String persistenceUnitName,
			EntityManagerFactory entityManagerFactory) {

		emfMap.put(persistenceUnitName, entityManagerFactory);

	}

	/**
	 * 
	 * @return
	 */
	private static Map<String, EntityManager> getEntityManagersMap() {
		Map<String, EntityManager> entityManagersMap = emMaps.get();
		if (null == entityManagersMap) {
			entityManagersMap = new HashMap<String, EntityManager>();
			emMaps.set(entityManagersMap);
		}
		return entityManagersMap;
	}

	/**
	 * get entityManager
	 * 
	 * @param persistenceUnitName
	 * @return
	 */
	public static EntityManager getEntityManager(String persistenceUnitName) {

		Map<String, EntityManager> entityManagersMap = getEntityManagersMap();

		EntityManager em = entityManagersMap.get(persistenceUnitName);
		if ((null == em) || (!em.isOpen())) {
			EntityManagerFactory emFactory = getEntityManagerFactory(persistenceUnitName);
			em = emFactory.createEntityManager();
			// register the new entity manager
			setEntityManager(persistenceUnitName, em);
		}
		return em;
	}
	
	/**
	 * 
	 * @param persistenceUnitName
	 * @return
	 */
	public static EntityManager getNewEntityManager(String persistenceUnitName) {

		EntityManagerFactory emFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
		
		EntityManager em = emFactory.createEntityManager();
		return em;
		
	}
	
	/**
	 * 
	 * @param persistenceUnitName
	 * @return
	 */
	public static EntityManagerFactory getNewEntityManagerFactory(String persistenceUnitName) {

		EntityManagerFactory emFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
		return emFactory;
		
	}

	/**
	 * 
	 * @param persistenceUnitName
	 * @return
	 */
	public static EntityManagerFactory getEntityManagerFactory(
			String persistenceUnitName) {
		EntityManagerFactory emFactory = emfMap.get(persistenceUnitName);
		if (null == emFactory) {
			synchronized (emfToken) {
				emFactory = emfMap.get(persistenceUnitName);
				if (null == emFactory) {
					emFactory = Persistence
							.createEntityManagerFactory(persistenceUnitName);
					emfMap.put(persistenceUnitName, emFactory);
				}
			}
		}
		return emFactory;
	}

	/**
	 * close the entityManager
	 * 
	 * @param persistenceUnitName
	 */
	public static void close(String persistenceUnitName) {

		Map<String, EntityManager> entityManagersMap = getEntityManagersMap();
		EntityManager em = entityManagersMap.get(persistenceUnitName);
		if (null != em) {
			if (em.isOpen()) {
				try {
					em.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			em = null;
			// register the new entity manager
			setEntityManager(persistenceUnitName, null);
		}
	}

	/**
	 * flush the entityManager
	 * @param persistenceUnitName
	 */
	public static void flush(String persistenceUnitName) {

		Map<String, EntityManager> entityManagersMap = getEntityManagersMap();
		EntityManager em = entityManagersMap.get(persistenceUnitName);
		if (null != em) {
			if (em.isOpen()) em.flush();
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
				if (em.isOpen()) {
					try {
						em.close();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
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
