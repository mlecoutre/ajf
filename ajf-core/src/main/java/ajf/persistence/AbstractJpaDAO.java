package ajf.persistence;

import javax.persistence.EntityManager;

public abstract class AbstractJpaDAO extends AbstractDAO implements JpaDAO {

	// the EntityManager 
	protected EntityManager entityManager = null;
	
	/*
	 * @see ajf.persistence.JpaDAO#getEntityManager()
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/*
	 * @see ajf.persistence.JpaDAO#setEntityManager(javax.persistence.EntityManager)
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
}
