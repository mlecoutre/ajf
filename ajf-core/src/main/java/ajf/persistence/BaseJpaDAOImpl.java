package ajf.persistence;

import javax.persistence.PersistenceException;

public class BaseJpaDAOImpl extends AbstractJpaDAO {
	
	/**
	 * 
	 */
	public BaseJpaDAOImpl() {
		super();
	}
	
	/**
	 * 
	 * @param bean
	 * @throws PersistenceException
	 */
	public boolean add(Object bean) throws PersistenceException {
		this.entityManager.persist(bean);
		this.entityManager.flush();
		return true;
	}
	
	/**
	 * 
	 * @param bean
	 * @throws PersistenceException
	 */
	public boolean update(Object bean) throws PersistenceException {
		this.entityManager.persist(bean);
		this.entityManager.flush();
		return true;
	}
	
	/**
	 * 
	 * @param bean
	 * @throws PersistenceException
	 */
	public boolean remove(Object bean) throws PersistenceException {
		this.entityManager.remove(bean);
		this.entityManager.flush();
		return true;
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param pk
	 * @throws PersistenceException
	 */
	public boolean removeByPrimaryKey(Class<?> entityClass, Object pk) throws PersistenceException {
		Object bean = findByPrimaryKey(entityClass, pk);
		if (null != bean) {
			remove(bean);
			return true;
		}
		return false;
		
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param pk
	 * @return
	 * @throws PersistenceException
	 */
	public Object findByPrimaryKey(Class<?> entityClass, Object pk)
		throws PersistenceException {
		Object res = this.entityManager.find(entityClass, pk);
		return res;
	}
	
}
