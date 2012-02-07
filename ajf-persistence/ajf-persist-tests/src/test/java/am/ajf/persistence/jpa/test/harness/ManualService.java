package am.ajf.persistence.jpa.test.harness;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

public class ManualService implements ManualServiceBD {

	@Inject
	private EntityManagerFactory emf;
	
	public List<ModelManual> findByNameOrderBy(String name, String order) {
		EntityManager em = emf.createEntityManager();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT m FROM ModelManual m WHERE m.lastName = :name");
		if (order != null) {
			sb.append(" ORDER BY ");
			sb.append(order);
		}
		
		Query query = em.createQuery(sb.toString());
		query.setParameter("name", name);
		
		return query.getResultList();
	}
	
	public void insertNew(ModelManual model) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(model);
		em.getTransaction().commit();		
	}	
}
