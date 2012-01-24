package ajf.sample.jpa.tomcat;

import java.util.List;
import java.util.Random;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.transaction.Transactional;

@RequestScoped
public class SamplePolicy /*implements SamplePolicyBD*/ {

	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Inject private SampleServiceBD sampleService;
	@Inject private UserTransaction utx;
	@Inject private EntityManagerFactory emf;
	
	//@Override
	public ListAllModelsRB listAllModels() {
		logger.debug("SamplePolicy : listAllModels");
		ListAllModelsRB listAllModelsRB = new ListAllModelsRB();
		List<Model> models = sampleService.findAllModels();
		listAllModelsRB.setModels(models);		
		return listAllModelsRB;
	}

	//@Override
	@Transactional
	public void createModelCrud() {
		int modelId = new Random().nextInt(); 
		logger.debug("SamplePolicy : create a new Model with Crud service("+modelId+")");
		sampleService.save(new Model("model : "+modelId));		
	}
	
	//@Override	
	public void createModelManual() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		utx.begin();
		int modelId = new Random().nextInt(); 
		logger.debug("SamplePolicy : Manual create a new Model ("+modelId+")");
		EntityManager em = emf.createEntityManager();
		em.persist(new Model("model - manual : "+modelId));
		utx.commit();		
	}
	
	
	
}
