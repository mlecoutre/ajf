package ajf.sample.jpa.tomcat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.LogManager;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ajf.persistence.jpa.EntityManagerProvider;

//@ManagedBean(name="jpaBean")
@Named("jpaBean")
@RequestScoped
public class JpaMBean implements Serializable {

	private static final long serialVersionUID = 1687667378623102828L;
	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Inject private SamplePolicyBD policy;
	
	private List<Model> models;
	
	public JpaMBean() {
		//nothing, but needed for passivation.
	}
	
	@PostConstruct
	public void init() {
		models = new ArrayList<Model>();
	}

	public void computeModels(ActionEvent event) {
		logger.debug("JpaMBean : compute models");
		models = policy.listAllModels().getModels();
		logger.debug("models changed. found "+models.size()+" value(s)");
	}
	
	public void createModel(ActionEvent event) {
		logger.debug("JpaMBean : creates models");
		EntityManager em = EntityManagerProvider.getEntityManager("default");
		
		em.getTransaction().begin();
		em.persist(new Model("model : "+new Random().nextInt()));
		em.getTransaction().commit();
		
		models = policy.listAllModels().getModels();
		logger.debug("models changed. found "+models.size()+" value(s)");
	}

	public List<Model> getModels() {
		return models;
	}
}