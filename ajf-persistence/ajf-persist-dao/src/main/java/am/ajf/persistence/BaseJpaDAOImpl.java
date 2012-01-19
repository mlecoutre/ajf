package am.ajf.persistence;

import java.util.List;

import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseJpaDAOImpl extends AbstractJpaDAO {

	private static Logger logger = LoggerFactory
			.getLogger(AbstractJpaDAO.class);

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
	public Object add(Object bean) throws PersistenceException {
		this.entityManager.persist(bean);
		this.entityManager.flush();
		return bean;
	}

	/**
	 * 
	 * @param bean
	 * @throws PersistenceException
	 */
	public Object create(Object bean) throws PersistenceException {
		return add(bean);
	}

	/**
	 * 
	 * @param bean
	 * @throws PersistenceException
	 */
	public Object update(Object bean) throws PersistenceException {
		this.entityManager.persist(bean);
		this.entityManager.flush();
		return bean;
	}

	/**
	 * 
	 * @param bean
	 * @throws PersistenceException
	 */
	public Object remove(Object bean) throws PersistenceException {
		this.entityManager.remove(bean);
		this.entityManager.flush();
		return bean;
	}

	/**
	 * 
	 * @param bean
	 * @throws PersistenceException
	 */
	public Object delete(Object bean) throws PersistenceException {
		return remove(bean);
	}

	/**
	 * 
	 * @param entityClass
	 * @param pk
	 * @throws PersistenceException
	 */
	public Object removeByPrimaryKey(Class<?> entityClass, Object pk)
			throws PersistenceException {
		Object bean = findByPrimaryKey(entityClass, pk);
		if (null != bean) {
			return remove(bean);
		}
		return null;

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
		// detach the bean
		return res;
	}

	/**
	 * 
	 * @param entityClass
	 * @return
	 * @throws PersistenceException
	 */
	public List<?> findAll(Class<?> entityClass) throws PersistenceException {

		Query query = this.entityManager.createQuery("from ".concat(entityClass
				.getName()));
		return (List<?>) processFindQuery(entityClass, 
				entityClass.getSimpleName().concat(".findAll"), query, false);

	}

	/**
	 * 
	 * @param entityClass
	 * @param queryName
	 * @param args
	 * @return
	 * @throws PersistenceException
	 */
	public List<?> findQuery(Class<?> entityClass, String queryName,
			Object[] args, String[] argNames) throws PersistenceException {

		Query query = retrieveInitializedQuery(queryName, args, argNames);
		return (List<?>) processFindQuery(entityClass, queryName, query, false);

	}

	/**
	 * 
	 * @param entityClass
	 * @param queryName
	 * @param args
	 * @return
	 * @throws PersistenceException
	 */
	public Object findSingleResultQuery(Class<?> entityClass, String queryName,
			Object[] args, String[] argNames) throws PersistenceException {

		Query query = retrieveInitializedQuery(queryName, args, argNames);
		Object result = processFindQuery(entityClass, queryName, query, true);
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
	public boolean executeQuery(Class<?> entityClass, String queryName,
			Object[] args, String[] argNames) throws PersistenceException {

		Query query = retrieveInitializedQuery(queryName, args, argNames);
		int num = query.executeUpdate();
		this.entityManager.flush();
		return (num > 0);

	}

	/**
	 * 
	 * @param query
	 * @return
	 */
	private Object processFindQuery(Class<?> entityClass, String queryName, Query query,
			boolean single) throws PersistenceException {
		try {
			Object result = null;
			if (!single) {
				if (-1 != this.getFirstResult()) {
					query.setFirstResult(this.getFirstResult());
					query.setMaxResults(this.getMaxResults());
				}
				result = query.getResultList();
			} else {
				try {
					result = query.getSingleResult();
				} catch (NonUniqueResultException e) {
					logger.warn(
							"The namedQuery '"
									+ queryName
									+ "' return a non unique result, try to return the first result.",
							e);
					query.setFirstResult(0);
					query.setMaxResults(1);
					List<?> resultList = query.getResultList();
					if ((null != resultList) && (!resultList.isEmpty())) {
						result = resultList.get(0);
					}
				}
			}
						
			return result;
		} catch (Exception e) {
			String message = "Unexpected exception while trying to process the namedQuery '"
					+ queryName + "'.";
			logger.error(message, e);
			throw new PersistenceException(message, e);
		}
	}

	/**
	 * 
	 * @param queryName
	 * @param args
	 * @return
	 */
	private Query retrieveInitializedQuery(String queryName, Object[] args, String[] argNames)
			throws PersistenceException {
		Query query;
		try {
			query = this.entityManager.createNamedQuery(queryName);
		} catch (Throwable e) {
			String message = "The namedQuery '" + queryName
					+ "' can not be found.";
			logger.error(message, e);
			throw new PersistenceException(message, e);
		}
		if (null != args) {
			for (int i = 0; i < args.length; i++) {
				Object value = args[i];
				String parameterName = "p".concat(String.valueOf(i + 1));
				if ((null != argNames) && (argNames.length > 0))
					parameterName = argNames[i];				
				query.setParameter(parameterName, value);
			}
		}
		return query;
	}
	
	

}
