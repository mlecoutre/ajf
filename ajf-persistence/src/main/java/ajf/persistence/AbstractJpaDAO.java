package ajf.persistence;

import javax.persistence.EntityManager;

public abstract class AbstractJpaDAO extends AbstractDAO implements JpaDAO {

	// the EntityManager 
	protected EntityManager entityManager = null;
	
	/*
	protected boolean detachEntities = false;
	*/
	
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

	/*
	public boolean isDetachEntities() {
		return detachEntities;
	}

	public void setDetachEntities(boolean detachEntities) {
		this.detachEntities = detachEntities;
	}
	*/
		
}
