package am.ajf.showcase.core.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;

import am.ajf.core.services.exceptions.BusinessLayerException;
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
	public boolean update(Person person) throws BusinessLayerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeByPrimaryKey(Long personId)
			throws BusinessLayerException {

		try {

			EntityTransaction tx = em.getTransaction();
			tx.begin();
			Person p = em.find(Person.class, personId);
			em.remove(p);
			tx.commit();
		} catch (Exception ple) {
			logger.error("create method: " + ple.getMessage(), ple);
			throw new BusinessLayerException(ple);
		}
		return true;
	}

	@Override
	public Person findByPersonid(Long personid) throws BusinessLayerException {

		try {

			return (Person) em.createNamedQuery(Person.FIND_BY_PERSONID)
					.setParameter("personid", personid).getSingleResult();
		} catch (Exception ple) {
			logger.error("findByPersonid method: " + ple.getMessage(), ple);
			throw new BusinessLayerException(ple);
		}
	}

	@Override
	public boolean create(Person person) throws BusinessLayerException {

		try {

			EntityTransaction tx = em.getTransaction();
			tx.begin();
			em.persist(person);
			tx.commit();
		} catch (Exception ple) {
			logger.error("create method: " + ple.getMessage(), ple);
			throw new BusinessLayerException(ple);
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Person> findByLastname(String lastname)
			throws BusinessLayerException {
		logger.debug("findByLastname: " + lastname);

		try {

			return em.createNamedQuery(Person.FIND_BY_LASTNAME)
					.setParameter("lastname", lastname).getResultList();
		} catch (Exception ple) {
			logger.error("findByPersonid method: " + ple.getMessage(), ple);
			throw new BusinessLayerException(ple);
		}
	}
}
