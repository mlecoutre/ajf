package am.ajf.persistence.jpa;

import java.util.List;

/**
 * Database CRUD operations definition for use in a service. 
 * 
 * 
 * @author Nicolas Radde (E016696)
 *
 * @param <E> The JPA Entity type
 * @param <P> The JPA Entity primary key type
 */
public interface CrudServiceBD<E,P> {
	
	List<E> find(String queryName, Object... params); 
	E save(E entity);
	boolean remove(E entity);
	
	//TODO Temporary removal until a solution is found to use generics in the javaassist impl
	/*
	boolean delete(P pk);
	E fetch(P pk);
	*/ 

}
