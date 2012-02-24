package am.ajf.injection.harness;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import am.ajf.injection.Transactional;
import am.ajf.injection.helper.EntityManagerProvider;

@Transactional
public class JPAQueriesOnDBH2Policy {
	
	@SuppressWarnings("unchecked")
	public ModelRB findAllModels() throws IllegalArgumentException, IllegalAccessException {
		EntityManagerFactory emf1 = EntityManagerProvider.getEntityManagerFactoryH21();
		EntityManager em1 = emf1.createEntityManager();
		List<Model1> res1 = em1.createQuery("SELECT m FROM Model1 m").getResultList();
		
		EntityManagerFactory emf2 = EntityManagerProvider.getEntityManagerFactoryH22();
		EntityManager em2 = emf2.createEntityManager();
		List<Model2> res2 = em2.createQuery("SELECT m FROM Model2 m").getResultList();
		
		ModelRB rb = new ModelRB();
		rb.setModels1(res1);
		rb.setModels2(res2);
		return rb;		
		
	}
	
	public void insert4And4Models() throws IllegalArgumentException, IllegalAccessException {
		EntityManagerFactory emf = EntityManagerProvider.getEntityManagerFactoryH21();
		EntityManager em = emf.createEntityManager();
		for (int i=1 ; i < 5 ; i++) { 
			em.persist(new Model1("model h2 no 1 : "+i));
		}		
		EntityManagerFactory emf2 = EntityManagerProvider.getEntityManagerFactoryH22();
		EntityManager em2 = emf2.createEntityManager();
		for (int i=1 ; i < 5 ; i++) { 
			em2.persist(new Model2("model h2 no 2 : "+i));
		}
	}
	
	public void insert4ModelsWithErrorThen4() throws Exception{
		EntityManagerFactory emf = EntityManagerProvider.getEntityManagerFactoryH21();
		EntityManager em = emf.createEntityManager();
		for (int i=1 ; i < 5 ; i++) { 
			em.persist(new Model1("model : "+i));
		}
		exceptionGenerator();
		
		EntityManagerFactory emf2 = EntityManagerProvider.getEntityManagerFactoryH22();
		EntityManager em2 = emf2.createEntityManager();
		for (int i=1 ; i < 5 ; i++) { 
			em2.persist(new Model1("model : "+i));
		}
	}
	
	public void insert4And4ModelsThenError() throws Exception{
		EntityManagerFactory emf = EntityManagerProvider.getEntityManagerFactoryH21();
		EntityManager em = emf.createEntityManager();
		for (int i=1 ; i < 5 ; i++) { 
			em.persist(new Model1("model : "+i));
		}		
		
		EntityManagerFactory emf2 = EntityManagerProvider.getEntityManagerFactoryH22();
		EntityManager em2 = emf2.createEntityManager();
		for (int i=1 ; i < 5 ; i++) { 
			em2.persist(new Model1("model : "+i));
		}
		
		exceptionGenerator();
	}
	
	private void exceptionGenerator() throws Exception {
		throw new Exception("Expected error in insert4ModelWithError");
	}

}
