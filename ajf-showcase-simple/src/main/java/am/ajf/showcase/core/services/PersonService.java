package am.ajf.showcase.core.services;

import java.util.List;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;

import org.slf4j.Logger;

import ajf.logger.injection.InjectLogger;
import ajf.services.exceptions.BusinessLayerException;
import am.ajf.showcase.lib.model.Person;
import am.ajf.showcase.lib.services.PersonServiceBD;

/**
 * PersonService implementation
 * 
 * @author E010925
 * 
 */
@Singleton
public class PersonService implements PersonServiceBD {

	@InjectLogger
	private Logger logger;

	@PersistenceUnit(name = "default")
	private EntityManagerFactory emFactory;

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
		EntityManager em = null;
		try {
			em = emFactory.createEntityManager();
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			Person p = em.find(Person.class, personId);
			em.remove(p);
			tx.commit();
		} catch (Exception ple) {
			logger.error("create method: " + ple.getMessage(), ple);
			throw new BusinessLayerException(ple);
		} finally {
			if ((null != em) && (em.isOpen()))
				em.close();
		}
		return true;
	}

	@Override
	public Person findByPersonid(Long personid) throws BusinessLayerException {
		EntityManager em = null;
		try {
			em = emFactory.createEntityManager();
			return (Person) em.createNamedQuery(Person.FIND_BY_PERSONID)
					.setParameter("personid", personid).getSingleResult();
		} catch (Exception ple) {
			logger.error("findByPersonid method: " + ple.getMessage(), ple);
			throw new BusinessLayerException(ple);
		} finally {
			if ((null != em) && (em.isOpen()))
				em.close();
		}
	}

	@Override
	public boolean create(Person person) throws BusinessLayerException {
		EntityManager em = null;
		try {
			em = emFactory.createEntityManager();
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			em.persist(person);
			tx.commit();
		} catch (Exception ple) {
			logger.error("create method: " + ple.getMessage(), ple);
			throw new BusinessLayerException(ple);
		} finally {
			if ((null != em) && (em.isOpen()))
				em.close();
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Person> findByLastname(String lastname)
			throws BusinessLayerException {
		logger.debug("findByLastname: " + lastname);
		EntityManager em = null;
		try {
			em = emFactory.createEntityManager();
			return em.createNamedQuery(Person.FIND_BY_LASTNAME)
					.setParameter("lastname", lastname).getResultList();
		} catch (Exception ple) {
			logger.error("findByPersonid method: " + ple.getMessage(), ple);
			throw new BusinessLayerException(ple);
		} finally {
			if ((null != em) && (em.isOpen()))
				em.close();
		}
	}
}
