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
		if (null == em) {
			EntityManagerFactory emFactory = Persistence
				.createEntityManagerFactory(persistenceUnitName);
			em = emFactory.createEntityManager();
			// remove auto-update
			em.setFlushMode(FlushModeType.COMMIT);
			// register the new entity manager
			entityManagersMap.put(persistenceUnitName, em);
		}
		return em;
	}
	
	
}
