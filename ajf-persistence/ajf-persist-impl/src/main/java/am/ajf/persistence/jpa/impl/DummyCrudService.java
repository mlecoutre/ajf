package am.ajf.persistence.jpa.impl;

import java.util.List;

import javax.enterprise.inject.Alternative;

import am.ajf.persistence.jpa.CrudServiceBD;

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
	public List<E> find(String queryName, Object... params) {		
		return null;
	}

	@Override
	public E save(E entity) {		
		return null;
	}

	@Override
	public boolean remove(E entity) { 
		return false;
	}

	@Override
	public boolean delete(P pk) {
		return false;
	}

	@Override
	public E fetch(P pk) {
		return null;
	}
		   
	 
}
