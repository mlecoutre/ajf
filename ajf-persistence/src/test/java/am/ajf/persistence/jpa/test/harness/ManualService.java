package am.ajf.persistence.jpa.test.harness;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import am.ajf.persistence.jpa.annotation.PersistenceUnit;

public class ManualService implements ManualServiceBD {

	@Inject
	@PersistenceUnit("jpa2")
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public List<ModelManual> findByNameOrderBy(String name, String order) {
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
		em.getTransaction().begin();
		em.persist(model);
		em.getTransaction().commit();		
	}	
}
