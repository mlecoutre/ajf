package ajf.persistence;

import javax.persistence.EntityManager;

public interface JpaDAO extends DAO {
	
	/**
	 * set the EntityManager
	 * @param entityManager
	 */
	void setEntityManager(EntityManager entityManager);
	
	/**
	 * get the EntityManager
	 * @return entityManager
	 */
	EntityManager getEntityManager();
	
	/**
	 * @return true if the entities has to bedetached
	 */
	boolean isDetachEntities();

	/**
	 * set the detach entities status
	 * @param detachEntities
	 */
	void setDetachEntities(boolean detachEntities);
	
	
}
