package am.ajf.persistence.jpa.impl;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import am.ajf.persistence.jpa.CrudServiceBD;

public class CrudService<E, P> implements CrudServiceBD<E, P> {

	private @Inject EntityManager em;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<E> find(String queryName, Object... params) {		
		return (List<E>) BasicImplCrudDbService.find(em, queryName, params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public E save(E entity) {		
		return (E) BasicImplCrudDbService.save(true, em, entity);
	}

	@Override
	public boolean remove(E entity) { 
		return BasicImplCrudDbService.remove(true, em, entity);
	}

	
	
}
