package ajf.persistence.jpa;

import java.util.List;

public interface CrudDbService<E,P> {
	
	List<E> find(String queryName, Object... params); 
	E save(E entity);
	boolean remove(E entity);
	boolean delete(P pk);
	E fetch(P pk); 

}
