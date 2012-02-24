package am.ajf.injection.harness;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import am.ajf.injection.Transactional;
import am.ajf.injection.helper.EntityManagerProvider;

@Transactional
public class JPAQueriesOnDBHsqlPolicy {
	
	@SuppressWarnings("unchecked")
	public List<Model1> findAllModel() throws IllegalArgumentException, IllegalAccessException {
		EntityManagerFactory emf = EntityManagerProvider.getEntityManagerFactory1();
		EntityManager em = emf.createEntityManager();
		List<Model1> res = em.createQuery("SELECT m FROM Model1 m").getResultList(); 
		return res;		
	}
	
	public void insert4Models() throws IllegalArgumentException, IllegalAccessException {
		EntityManagerFactory emf = EntityManagerProvider.getEntityManagerFactory1();
		EntityManager em = emf.createEntityManager();
		for (int i=1 ; i < 5 ; i++) { 
			em.persist(new Model1("model : "+i));
		}		
	}
	
	public void insert4ModelsWithError() throws Exception{
		EntityManagerFactory emf = EntityManagerProvider.getEntityManagerFactory1();
		EntityManager em = emf.createEntityManager();
		for (int i=1 ; i < 5 ; i++) { 
			em.persist(new Model1("model : "+i));
		}
		throw new Exception("Expected error in insert4ModelWithError");
	}

}
