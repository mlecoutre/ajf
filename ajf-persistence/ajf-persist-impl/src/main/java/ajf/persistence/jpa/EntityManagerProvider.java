package ajf.persistence.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public abstract class EntityManagerProvider {
	
	/**
	 * 
	 * @param persistenceUnitName
	 * @return
	 */
	public static EntityManager createEntityManager(String persistenceUnitName) {

		EntityManagerFactory emFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
		
		EntityManager em = emFactory.createEntityManager();
		return em;
		
	}
	
	/**
	 * 
	 * @param persistenceUnitName
	 * @return
	 */
	public static EntityManagerFactory createEntityManagerFactory(String persistenceUnitName) {

		EntityManagerFactory emFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
		return emFactory;
		
	}
	

}
