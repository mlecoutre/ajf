package am.ajf.persistence.jpa.impl;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import am.ajf.persistence.jpa.CrudBD;
import am.ajf.persistence.jpa.CrudServiceBD;
import am.ajf.persistence.jpa.EntityManagerProvider;
import am.ajf.persistence.jpa.annotation.PersistenceUnit;

public class CrudProvider<E, P> {
	
	private @Inject EntityManager em;
	private @Inject UserTransaction utx;
	
	/**
	 * TODO make this Producer with a better scope than @Dependent 
	 * @param ip
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Produces
	public CrudBD<E, P> createPolicy(InjectionPoint ip) {
		//Get the actual entity type from the declaration on the IP
		//If the IP doesnt declare the actual type but is also a parameterized type
		//this WILL fail.
		ParameterizedType type = (ParameterizedType) ip.getType();
		Class<E> entityClass = (Class<E>) type.getActualTypeArguments()[0];//first type is the Entity

		CrudBD<E, P> crudPolicy = new TransactionalCrudPolicy<E, P>(entityClass, em, utx);
		return crudPolicy;
	}
	
	/**
	 * TODO make this Producer with a better scope than @Dependent 
	 * @param ip
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Produces
	public CrudServiceBD<E, P> createService(InjectionPoint ip) {
		//Get the actual entity type from the declaration on the IP
		//If the IP doesnt declare the actual type but is also a parameterized type
		//this WILL fail.
		ParameterizedType type = (ParameterizedType) ip.getType();
		final Class<E> entityClass = (Class<E>) type.getActualTypeArguments()[0];//first type is the Entity
		
		// TODO Manage the PersistenceUnits annotations
		/*
		PersistenceUnit pu = ip.getMember().getDeclaringClass().getAnnotation(PersistenceUnit.class);		
		String persistenceUnitName = null;
		if (pu != null) {
			persistenceUnitName = pu.value();
		} else {
			persistenceUnitName = EntityManagerProvider.getDefaultPersistenceUnitName();
		}*/
		final boolean manageTransaction = EntityManagerProvider.getTransactionType(EntityManagerProvider.getDefaultPersistenceUnitName()) == EntityManagerProvider.TransactionType.LOCAL; 

		CrudServiceBD<E, P> crudService = new CrudServiceBD<E, P>() {					 			
			
			@Override
			public List<E> find(String queryName, Object... params) {		
				return (List<E>) BasicImplCrudDbService.find(em, queryName, params);
			}

			@Override
			public E save(E entity) {		
				return (E) BasicImplCrudDbService.save(manageTransaction, em, entity);
			}

			@Override
			public boolean remove(E entity) { 
				return BasicImplCrudDbService.remove(manageTransaction, em, entity);
			}

			@Override
			public boolean delete(P pk) {
				return BasicImplCrudDbService.delete(manageTransaction, em, entityClass, pk);
			}

			@Override
			public E fetch(P pk) {
				return BasicImplCrudDbService.fetch(em, entityClass, pk);
			}
		   
	   };	   
	   return crudService;
	}
}
