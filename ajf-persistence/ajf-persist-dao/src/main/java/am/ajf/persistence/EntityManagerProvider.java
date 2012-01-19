package am.ajf.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerProvider {
	
	private EntityManagerProvider() {
		super();
	}

	/**
	 * 
	 * @param persistenceUnitName
	 * @return
	 */
	public static EntityManager newEntityManager(String persistenceUnitName) {

		EntityManagerFactory emFactory = newEntityManagerFactory(persistenceUnitName);
		EntityManager em = emFactory.createEntityManager();
		return em;
		
	}
	
	/**
	 * 
	 * @param persistenceUnitName
	 * @return
	 */
	public static EntityManagerFactory newEntityManagerFactory(String persistenceUnitName) {

		EntityManagerFactory emFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
		return emFactory;
		
	}
	
}
