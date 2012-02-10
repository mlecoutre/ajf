package am.ajf.persistence.jpa.impl;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import am.ajf.persistence.jpa.CrudServiceBD;

public class CrudServiceProvider<E, P> {
	
	private @Inject EntityManager em;
	
	/**
	 * TODO make this Producer with a better scope than @Dependent 
	 * @param ip
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Produces
	public CrudServiceBD<E, P> create(InjectionPoint ip) {
		//Get the actual entity type from the declaration on the IP
		//If the IP doesnt declare the actual type but is also a parameterized type
		//this WILL fail.
		ParameterizedType type = (ParameterizedType) ip.getType();
		final Class<E> entityClass = (Class<E>) type.getActualTypeArguments()[0];//first type is the Entity

		CrudServiceBD<E, P> crudService = new CrudServiceBD<E, P>() {			
		   
			@Override
			public List<E> find(String queryName, Object... params) {		
				return (List<E>) BasicImplCrudDbService.find(em, queryName, params);
			}

			@Override
			public E save(E entity) {		
				return (E) BasicImplCrudDbService.save(true, em, entity);
			}

			@Override
			public boolean remove(E entity) { 
				return BasicImplCrudDbService.remove(true, em, entity);
			}

			@Override
			public boolean delete(P pk) {
				return BasicImplCrudDbService.delete(true, em, entityClass, pk);
			}

			@Override
			public E fetch(P pk) {
				return BasicImplCrudDbService.fetch(em, entityClass, pk);
			}
		   
	   };	   
	   return crudService;
	}
}
