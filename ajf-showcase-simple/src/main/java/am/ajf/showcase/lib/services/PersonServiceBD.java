package am.ajf.showcase.lib.services;

import java.util.List;

import ajf.services.exceptions.BusinessLayerException;
import am.ajf.showcase.lib.model.Person;

/**
 * Technical service PersonServiceBD
 * 
 * @author E010925
 * 
 */
public interface PersonServiceBD {

	boolean create(Person person) throws BusinessLayerException;

	boolean update(Person person) throws BusinessLayerException;

	boolean removeByPrimaryKey(Long personId) throws BusinessLayerException;

	Person findByPersonid(Long personid) throws BusinessLayerException;

	List<Person> findByLastname(String lastname) throws BusinessLayerException;

}
