package ajf.persistence;

import javax.persistence.EntityManager;

public abstract class AbstractJpaDAO extends AbstractDAO implements JpaDAO {

	protected EntityManager entityManager = null;
		
	/*
	 * (non-Javadoc)
	 * @see adc.persistence.JpaDAO#getEntityManager()
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/*
	 * (non-Javadoc)
	 * @see adc.persistence.JpaDAO#setEntityManager(javax.persistence.EntityManager)
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
}
