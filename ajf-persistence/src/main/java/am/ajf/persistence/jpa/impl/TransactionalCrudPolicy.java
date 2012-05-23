package am.ajf.persistence.jpa.impl;

import java.util.List;

import javax.enterprise.inject.Alternative;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import am.ajf.persistence.jpa.EntityManagerProvider;
import am.ajf.persistence.jpa.api.CrudBD;

@Alternative
public class TransactionalCrudPolicy<E, P> implements CrudBD<E, P> {

	private transient EntityManager em;
	private transient Class<E> entityClass;
	private UserTransaction utx;
	boolean manageTransaction;

	public TransactionalCrudPolicy(Class<E> entityClass, EntityManager em, UserTransaction utx) {
		this.entityClass = entityClass;
		this.em = em;
		this.utx = utx;
		manageTransaction = EntityManagerProvider.getTransactionType(EntityManagerProvider.getDefaultPersistenceUnitName()) == EntityManagerProvider.TransactionType.LOCAL;
	}
	
	@SuppressWarnings("unchecked")
	@Override	
	public List<E> find(String queryName, Object... params) {		
		return (List<E>) BasicImplCrudDbService.find(em, queryName, params);
	}
	
	@SuppressWarnings("unchecked")
	@Override	
	public List<E> page(String queryName, int firstResult, int maxNbResults, Object... params) {		
		return (List<E>) BasicImplCrudDbService.page(em, queryName, firstResult, maxNbResults, params);
	}
		
	@Override	
	public long count(String queryName, Object... params) {		
		return BasicImplCrudDbService.count(em, queryName, params);
	}

	@SuppressWarnings("unchecked")
	@Override	
	public E save(E entity) throws Throwable {
		E res = null;
		utx.begin();
		try {
			res = (E) BasicImplCrudDbService.save(manageTransaction, em, entity);
			utx.commit();
		} catch (Throwable t) {
			utx.rollback();
			throw t;
		}
		return res;
	}

	@Override	
	public boolean remove(E entity) throws Throwable { 
		boolean res;
		utx.begin();
		try {
			res = BasicImplCrudDbService.remove(manageTransaction, em, entity);
			utx.commit();
		} catch (Throwable t) {
			utx.rollback();
			throw t;
		}
		return res;
	}

	@Override	
	public boolean delete(P pk) throws Throwable {
		boolean res;
		utx.begin();
		try {
			res = BasicImplCrudDbService.delete(manageTransaction, em, entityClass, pk);
			utx.commit();
		} catch (Throwable t) {
			utx.rollback();
			throw t;
		}
		return res;
	}

	@Override	
	public E fetch(P pk) {
		return BasicImplCrudDbService.fetch(em, entityClass, pk);
	}
}
