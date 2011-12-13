package ajf.persistence.jpa;

import java.util.List;

public interface CrudDbService<E,PK> {
	
	List<E> find(String queryName, Object... params); 
	E save(E entity);
	//E add(E entity);
	//E update(E entity);
	boolean remove(E entity);
	boolean delete(PK pk);
	E fetch(PK pk); 

}
