package am.ajf.persistence.jpa.impl;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.cfg.NotYetImplementedException;

import am.ajf.persistence.jpa.CrudBD;
import am.ajf.persistence.jpa.CrudServiceBD;
import am.ajf.transaction.Transactional;

@Transactional
public class TransactionalCrudPolicy<E, P> implements CrudBD<E, P> {

	private @Inject CrudServiceBD<E,P> crudService ; 
		
	@Override
	public List<E> find(String queryName, Object... params) {		
		return crudService.find(queryName, params);
	}
	
	@Override
	public E save(E entity) {		
		return crudService.save(entity);
	}
	
	@Override
	public boolean remove(E entity) {		
		return crudService.remove(entity);
	}
	
	@Override
	public boolean delete(P pk) {
		return crudService.delete(pk);
	}

	@Override
	public E fetch(P pk) {
		return crudService.fetch(pk);
	}
}
