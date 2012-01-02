package ajf.persistence.jpa.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import ajf.persistence.jpa.EntityManagerProvider;

public class BasicImplCrudDbService {

	public static List<?> find(String queryName, Object... params) {
		EntityManager em = EntityManagerProvider.getEntityManager("default");
		Query q = em.createNamedQuery(queryName);
		for (int i = 0 ; i < params.length ; i++) {
			q.setParameter(i+1, params[i]);
		}
		return q.getResultList();
	}

	public static Object save(Object entity) {
		EntityManager em = EntityManagerProvider.getEntityManager("default");
		Object res = null;
		if (em.contains(entity)) {
			 res = em.merge(entity);
		} else {
			em.persist(entity);
			res = entity;
		}
		return res;
	}

	public static boolean remove(Object entity) {
		EntityManager em = EntityManagerProvider.getEntityManager("default");
		em.remove(entity);
		return true;
	}
	
	/*
	public static boolean delete(Object pk) {
		EntityManager em = EntityManagerProvider.getEntityManager("default");
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
	public static <E> E fetch(Object pk) {
		EntityManager em = EntityManagerProvider.getEntityManager("default");
		return em.find(E.class, pk);
	}
	*/

}
