package ajf.persistence;

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
	public boolean add(Object bean) throws PersistenceException {
		this.entityManager.persist(bean);
		this.entityManager.flush();
		// detach the bean
		/*
		if (this.detachEntities)
			this.entityManager.detach(bean);
		*/
		return true;
	}

	/**
	 * 
	 * @param bean
	 * @throws PersistenceException
	 */
	public boolean create(Object bean) throws PersistenceException {
		return add(bean);
	}

	/**
	 * 
	 * @param bean
	 * @throws PersistenceException
	 */
	public boolean update(Object bean) throws PersistenceException {
		this.entityManager.persist(bean);
		this.entityManager.flush();
		// detach the bean
		/*
		if (this.detachEntities)
			this.entityManager.detach(bean);
		*/
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
		// detach the bean
		/*
		if (this.detachEntities)
			this.entityManager.detach(bean);
		*/
		return true;
	}

	/**
	 * 
	 * @param bean
	 * @throws PersistenceException
	 */
	public boolean delete(Object bean) throws PersistenceException {
		return remove(bean);
	}

	/**
	 * 
	 * @param entityClass
	 * @param pk
	 * @throws PersistenceException
	 */
	public boolean removeByPrimaryKey(Class<?> entityClass, Object pk)
			throws PersistenceException {
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
		// detach the bean
		/*
		if (this.detachEntities)
			this.entityManager.detach(res);
		*/
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
			Object[] args) throws PersistenceException {

		Query query = retrieveInitializedQuery(queryName, args);
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
			Object[] args) throws PersistenceException {

		Query query = retrieveInitializedQuery(queryName, args);
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
			Object[] args) throws PersistenceException {

		Query query = retrieveInitializedQuery(queryName, args);
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
			
			/*
			if ((null != result) && this.detachEntities) {
				if (result instanceof List<?>) {
					if (!((List<?>) result).isEmpty()) {
						Object refBean = ((List<?>) result).get(0);
						try {
							refBean.getClass().asSubclass(entityClass);
							for (Iterator<?> iterator = ((List<?>) result).iterator(); iterator
									.hasNext();) {
								Object bean = iterator.next();
								// detach the bean
								this.entityManager.detach(bean);
							}
						} catch (Throwable e) {
							// Nothing to do
						}
					}
				}
				else {
					try {
						result.getClass().asSubclass(entityClass);
					} catch (Throwable e) {
						// Nothing to do
					}
					// detach the bean
					this.entityManager.detach(result);
				}
			}
			*/
			
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
	private Query retrieveInitializedQuery(String queryName, Object[] args)
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
				query.setParameter(parameterName, value);
			}
		}
		return query;
	}
	
	

}
