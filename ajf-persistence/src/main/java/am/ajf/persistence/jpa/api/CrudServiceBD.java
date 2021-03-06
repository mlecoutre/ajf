package am.ajf.persistence.jpa.api;

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
	
	/**
	 * Launch the selected NamedQuery on <E> with parameters
	 *  
	 * @param queryName
	 * @param params
	 * @return
	 */
	List<E> find(String queryName, Object... params);
	
	/**
	 * Launch the selected NamedQuery on <E> with parameters and
	 * paginate the results.
	 *  
	 * @param queryName the namedQuery name
	 * @param firstResult the index of the first result to fetch
	 * @param maxNbResults the maximum number of results to fetch
	 * @param params the optional parameters of the NamedQuery
	 * @return
	 */
	List<E> page(String queryName, int firstResult, int maxNbResults, Object... params);
	
	/**
	 * Launch the selected NamedQuery on <E> with parameters and
	 * and return the nb of results.
	 *  
	 * @param queryName the namedQuery name 
	 * @param params the optional parameters of the NamedQuery
	 * @return
	 */
	long count(String queryName, Object... params);
	
	/**
	 * Save the entity in the database and return the
	 * result of this operation.
	 * The returned entity is always attached.
	 * If the entity didnt exist, it will be created first.
	 * 
	 * @param entity
	 * @return
	 */
	E save(E entity);
	
	/**
	 * Delete the selected entity from the database.
	 * If the entity was detached, it is re-atached first.
	 * 
	 * @param entity
	 * @return
	 */
	boolean remove(E entity);
	
	/**
	 * Retrieve the entity specified with the primary key
	 * and removed it from the database.
	 * 
	 * @param pk
	 * @return
	 */
	boolean delete(P pk);
	
	/**
	 * Retrieve the selected entity from the database.
	 * The returned entity is attached.
	 * 
	 * @param pk
	 * @return
	 */
	E fetch(P pk);

}
