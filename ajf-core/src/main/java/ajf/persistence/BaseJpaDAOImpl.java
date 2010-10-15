package ajf.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

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
	
	/**
	 * 
	 * @param entityClass
	 * @return
	 * @throws PersistenceException
	 */
	public List<?> findAll(Class<?> entityClass) 
		throws PersistenceException {
		
		Query query = this.entityManager.createQuery("from ".concat(entityClass.getName()));
		return processFindQuery(query, false);
		
	}

	/**
	 * 
	 * @param query
	 * @return
	 */
	private List<?> processFindQuery(Query query, boolean single) {
		if (!single) {
			if (-1 != this.getFirstResult()) {
				query.setFirstResult(this.getFirstResult());
				query.setMaxResults(this.getMaxResults());
			}
		}
		else {
			query.setFirstResult(0);
			query.setMaxResults(1);
		}
		List<?> result = (List<?>) query.getResultList();
		return result;
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param queryName
	 * @param args
	 * @return
	 * @throws PersistenceException
	 */
	public List<?> findQuery(Class<?> entityClass, String queryName, Object[] args) 
		throws PersistenceException {
		
		Query query = retrieveInitializedQuery(queryName, args);
		return processFindQuery(query, false);
				
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param queryName
	 * @param args
	 * @return
	 * @throws PersistenceException
	 */
	public Object findSingleResultQuery(Class<?> entityClass, String queryName, Object[] args) 
		throws PersistenceException {
	
		Query query = retrieveInitializedQuery(queryName, args);
		List<?> result = processFindQuery(query, true);
		if ((null != result) && (!result.isEmpty())) 
			return result.get(0);
		return null;
			
	}

	/**
	 * 
	 * @param queryName
	 * @param args
	 * @return
	 */
	private Query retrieveInitializedQuery(String queryName, Object[] args) {
		Query query = this.entityManager.createNamedQuery(queryName);
		if (null != args) {
			for (int i = 0; i < args.length; i++) {
				Object value = args[i];
				String parameterName = "p".concat(String.valueOf(i+1));
				query.setParameter(parameterName, value);
			}
		}
		return query;
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param queryName
	 * @param args
	 * @return
	 * @throws PersistenceException
	 */
	public boolean executeQuery(Class<?> entityClass, String queryName, Object[] args) 
		throws PersistenceException {
	
		Query query = retrieveInitializedQuery(queryName, args);
		int num = query.executeUpdate();
		this.entityManager.flush();
		return (num > 0);
	
	}
	
}
