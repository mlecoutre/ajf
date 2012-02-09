package am.ajf.persistence.jpa.impl;

import java.util.List;

import javax.inject.Inject;

import am.ajf.persistence.jpa.CrudBD;
import am.ajf.persistence.jpa.CrudServiceBD;
import am.ajf.transaction.Transactional;

public class TransactionalCrudPolicy<E, P> implements CrudBD<E, P> {

	private @Inject CrudServiceBD<E,P> crudService ; 
	
	@Transactional
	@Override
	public List<E> find(String queryName, Object... params) {		
		return crudService.find(queryName, params);
	}

	@Transactional
	@Override
	public E save(E entity) {		
		return crudService.save(entity);
	}

	@Transactional
	@Override
	public boolean remove(E entity) {		
		return crudService.remove(entity);
	}
}
