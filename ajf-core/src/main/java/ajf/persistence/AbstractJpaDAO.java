package ajf.persistence;

import javax.persistence.EntityManager;

public abstract class AbstractJpaDAO extends AbstractDAO implements JpaDAO {

	// the EntityManager 
	protected EntityManager entityManager = null;
	// detach option
	protected boolean detachEntities = false;	
	
	/*
	 * (non-Javadoc)
	 * @see ajf.persistence.JpaDAO#getEntityManager()
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/*
	 * (non-Javadoc)
	 * @see ajf.persistence.JpaDAO#setEntityManager(javax.persistence.EntityManager)
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/*
	 * (non-Javadoc)
	 * @see ajf.persistence.JpaDAO#isDetachEntities()
	 */
	public boolean isDetachEntities() {
		return this.detachEntities;
	}

	/*
	 * (non-Javadoc)
	 * @see ajf.persistence.JpaDAO#setDetachEntities(boolean)
	 */
	public void setDetachEntities(boolean detachEntities) {
		this.detachEntities = detachEntities;
	}
	
}
