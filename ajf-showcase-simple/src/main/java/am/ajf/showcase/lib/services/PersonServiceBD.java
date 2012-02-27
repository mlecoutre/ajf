package am.ajf.showcase.lib.services;

import java.util.List;

import am.ajf.core.services.exceptions.ServiceLayerException;
import am.ajf.showcase.lib.model.Person;

/**
 * Technical service PersonServiceBD
 * 
 * @author E010925
 * 
 */
public interface PersonServiceBD {

	boolean create(Person person) throws ServiceLayerException;

	boolean update(Person person) throws ServiceLayerException;

	boolean removeByPrimaryKey(Long personId) throws ServiceLayerException;

	Person findByPersonid(Long personid) throws ServiceLayerException;

	List<Person> findByLastname(String lastname) throws ServiceLayerException;

}
