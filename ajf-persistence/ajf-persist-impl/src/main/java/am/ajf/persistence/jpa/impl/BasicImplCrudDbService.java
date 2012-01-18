package am.ajf.persistence.jpa.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

public class BasicImplCrudDbService {

	public static List<?> find(EntityManagerFactory emf, String queryName, Object... params) {
		EntityManager em = emf.createEntityManager();
		Query q = em.createNamedQuery(queryName);
		for (int i = 0 ; i < params.length ; i++) {
			q.setParameter(i+1, params[i]);
		}
		return q.getResultList();
	}

	public static Object save(EntityManagerFactory emf, Object entity) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Object res = null;
		//if (em.contains(entity)) {
			 res = em.merge(entity);
		//} else {
			//em.persist(entity);
			//res = entity;
		//}
		em.getTransaction().commit();
		return res;
	}

	public static boolean remove(EntityManagerFactory emf, Object entity) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Object attachedEntity = em.merge(entity);  // need to reattached in local transaction
		em.remove(attachedEntity);
		em.getTransaction().commit();
		return true;
	}
	
	/*
	public static boolean delete(EntityManagerFactory emf, Object pk) {
		EntityManager em = emf.createEntityManager();
		Object obj = em.find(E.class, pk);
		if (obj != null) {
			em.remove(obj);
			return true;
		} else {
			return false;
		}		
	}
	*/

	/*
	public static <E> E fetch(EntityManagerFactory emf, Object pk) {
		EntityManager em = emf.createEntityManager();
		return em.find(E.class, pk);
	}
	*/

}
