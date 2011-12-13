package am.ajf.samples.jpa.was7;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ajf.persistence.jpa.EntityManagerProvider;

@Named(value="jpaBean")
@RequestScoped
public class OwbMB implements Serializable {
	
	private static final long serialVersionUID = 1687667378623102828L;
	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Inject private SamplePolicyBD policy;
	
	private List<Model> models;
	
	public OwbMB() {
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
	
	public int getModelListSize() {
		return models.size();
	}

}
