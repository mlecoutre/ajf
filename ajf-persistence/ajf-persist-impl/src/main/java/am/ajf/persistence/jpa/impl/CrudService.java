package am.ajf.persistence.jpa.impl;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import am.ajf.persistence.jpa.CrudServiceBD;

public class CrudService<E, P> implements CrudServiceBD<E, P> {

	private @Inject EntityManagerFactory emf;
	
	@Override
	public List<E> find(String queryName, Object... params) {		
		return (List<E>) BasicImplCrudDbService.find(emf, queryName, params);
	}

	@Override
	public E save(E entity) {		
		return (E) BasicImplCrudDbService.save(true, emf, entity);
	}

	@Override
	public boolean remove(E entity) { 
		return BasicImplCrudDbService.remove(true, emf, entity);
	}

	
	
}