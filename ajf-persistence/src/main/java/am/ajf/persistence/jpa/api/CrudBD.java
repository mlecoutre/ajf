package am.ajf.persistence.jpa.api;

import java.util.List;

public interface CrudBD<E,P> {
	
	
	List<E> find(String queryName, Object... params);
	List<E> page(String queryName, int firstResult, int maxNbResults, Object... params);
	long count(String queryName, Object... params);
	E save(E entity) throws Throwable;
	boolean remove(E entity) throws Throwable;
	boolean delete(P pk) throws Throwable;
	E fetch(P pk);

}
