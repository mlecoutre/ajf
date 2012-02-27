package am.ajf.showcase.core.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;

import am.ajf.core.services.exceptions.ServiceLayerException;
import am.ajf.showcase.lib.model.Person;
import am.ajf.showcase.lib.services.PersonServiceBD;

/**
 * PersonService implementation
 * 
 * @author E010925
 * 
 */
@ApplicationScoped
public class PersonService implements PersonServiceBD {

	@Inject
	private Logger logger;

	@Inject
	private EntityManager em;

	public PersonService() {
		super();
	}

	@Override
	public boolean update(Person person) throws ServiceLayerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeByPrimaryKey(Long personId)
			throws ServiceLayerException {

		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Person p = em.find(Person.class, personId);
		em.remove(p);
		tx.commit();

		return true;
	}

	@Override
	public Person findByPersonid(Long personid) throws ServiceLayerException {

		return (Person) em.createNamedQuery(Person.FIND_BY_PERSONID);

	}

	@Override
	public boolean create(Person person) throws ServiceLayerException {

		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(person);
		tx.commit();

		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Person> findByLastname(String lastname)
			throws ServiceLayerException {
		logger.debug("findByLastname: " + lastname);

		return em.createNamedQuery(Person.FIND_BY_LASTNAME)
				.setParameter("lastname", lastname).getResultList();

	}
}
