package am.ajf.transaction.test.helper;

import java.util.List;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

import am.ajf.transaction.test.harness.Model1;
import am.ajf.transaction.test.harness.Model2;

public class DBHelper {	
	
	public static void insertNames(EntityManagerFactory emf, int model, String... names) throws Exception {
		InitialContext ic = new InitialContext();
		UserTransaction ut = (UserTransaction)ic.lookup("java:comp/UserTransaction");
		EntityManager em = emf.createEntityManager();
		
		ut.begin();
		em.joinTransaction();
		for (String name : names) {
			if (model == 2) {
				em.persist(new Model2(name));
			} else if (model == 1) {
				em.persist(new Model1(name));
			}
		}		
		ut.commit();        		
	}
	
	public static void cleanDB12() throws Exception {
		InitialContext ic = new InitialContext();
		UserTransaction ut = (UserTransaction)ic.lookup("java:comp/UserTransaction");
		ut.begin();
		EntityManager em1 = EntityManagerProvider.getEntityManagerFactory1().createEntityManager();
		em1.joinTransaction();
        List<Model1> list1 = em1.createQuery("SELECT m FROM Model1 m").getResultList();
        for (Model1 model : list1) {
        	em1.remove(model);
        }        
        ut.commit();
        
        ut.begin();
        EntityManager em2 = EntityManagerProvider.getEntityManagerFactory2().createEntityManager();
        em2.joinTransaction();
        List<Model2> list2 = em2.createQuery("SELECT m FROM Model2 m").getResultList();
        for (Model2 model : list2) {
        	em2.remove(model);
        }
        ut.commit();                
	}
	
	public static void cleanDB34() throws Exception {
		InitialContext ic = new InitialContext();
		UserTransaction ut = (UserTransaction)ic.lookup("java:comp/UserTransaction");
		ut.begin();
		EntityManager em1 = EntityManagerProvider.getEntityManagerFactoryH21().createEntityManager();
		em1.joinTransaction();
        List<Model1> list1 = em1.createQuery("SELECT m FROM Model1 m").getResultList();
        for (Model1 model : list1) {
        	em1.remove(model);
        }        
        ut.commit();
        
        ut.begin();
        EntityManager em2 = EntityManagerProvider.getEntityManagerFactoryH22().createEntityManager();
        em2.joinTransaction();
        List<Model2> list2 = em2.createQuery("SELECT m FROM Model2 m").getResultList();
        for (Model2 model : list2) {
        	em2.remove(model);
        }
        ut.commit();                
	}

}
