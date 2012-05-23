package am.ajf.persistence.jpa.impl;

import java.util.List;

import javax.enterprise.inject.Alternative;

import am.ajf.persistence.jpa.api.CrudServiceBD;

/**
 * Dummy CrudService to stopped the auto-scan implementation of the Handler 
 * 
 * @author Nicolas Radde (E016696)
 *
 * @param <E>
 * @param <P>
 */
@Alternative
public class DummyCrudService<E, P>  implements CrudServiceBD<E, P>{
		
	@Override
	public long count(String queryName, Object... params) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<E> page(String queryName, int firstResult, int maxNbResults,
			Object... params) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public List<E> find(String queryName, Object... params) {		
		throw new UnsupportedOperationException();
	}

	@Override
	public E save(E entity) {		
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(E entity) { 
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean delete(P pk) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E fetch(P pk) {
		throw new UnsupportedOperationException();
	}
		   
	 
}
