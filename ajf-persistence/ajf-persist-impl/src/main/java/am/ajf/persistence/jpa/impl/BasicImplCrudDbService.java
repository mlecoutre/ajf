package am.ajf.persistence.jpa.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import am.ajf.persistence.jpa.CrudServiceBD;

public class BasicImplCrudDbService {

	public static List<?> find(EntityManager em, String queryName, Object... params) {
		//EntityManager em = emf.createEntityManager();
		Query q = em.createNamedQuery(queryName);
		for (int i = 0 ; i < params.length ; i++) {
			q.setParameter(i+1, params[i]);
		}
		return q.getResultList();
	}

	public static Object save(boolean manageTransaction, EntityManager em, Object entity) {
		boolean transActive = false;
		if (manageTransaction) {
			transActive = em.getTransaction().isActive();
			if (!transActive) {
				em.getTransaction().begin();
			}
		} else {
			em.joinTransaction();
		}
		Object res = em.merge(entity);
					
		if (manageTransaction) {
			if (!transActive) {
				em.getTransaction().commit();
			}
		}
		return res;
	}

	public static boolean remove(boolean manageTransaction, EntityManager em, Object entity) {
		boolean transActive = false;
		if (manageTransaction) {
			transActive = em.getTransaction().isActive();
			if (!transActive) {
				em.getTransaction().begin();
			}
		} else {
			em.joinTransaction();
		}
		
		Object attachedEntity = em.merge(entity);  // need to reattached in local transaction
		em.remove(attachedEntity);
		
		if (manageTransaction) {
			if (!transActive) {
				em.getTransaction().commit();
			}
		}
		return true;
	}
	
	public static <E> boolean delete(boolean manageTransaction, EntityManager em, Class<E> entityClass, Object pk) {
		//Manage the JTA vs RESOURCE_LOCAL and start/close a local transaction if
		//one is not active
		boolean transActive = false;
		if (manageTransaction) {
			transActive = em.getTransaction().isActive();
			if (!transActive) {
				em.getTransaction().begin();
			}
		} else {
			em.joinTransaction();
		}
		
		boolean res = false;
		Object obj = em.find(entityClass, pk);
		if (obj != null) {
			em.remove(obj);
			res = true;
		}				
		
		if (manageTransaction) {
			if (!transActive) {
				em.getTransaction().commit();
			}
		}
		return res;
	}
	

	
	public static <E> E fetch(EntityManager em, Class<E> entityClass, Object pk) {		
		return em.find(entityClass, pk);
	}
	
	/**
	 * Return the actual CrudServiceBD Generic instance used by the base service class
	 * This is used to resolve the Generics at runtime.
	 * @param baseClass the Service class
	 * @return a CrudServiceDB interface
	 */
	public static Type getCrudInterface(Class<?> baseClass) {
		for (Class<?> in : baseClass.getInterfaces()) {
			Class<?>[] interfaces = in.getInterfaces();
			for (int i = 0 ; i < interfaces.length ; i++) {
				Class<?> inSuper = interfaces[i];
				if (CrudServiceBD.class.equals(inSuper)) {
					return in.getGenericInterfaces()[i];
				}
			}			
		}
		return null;		
	}
	

}
