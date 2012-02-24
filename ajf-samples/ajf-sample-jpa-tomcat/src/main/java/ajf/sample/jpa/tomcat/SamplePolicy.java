package ajf.sample.jpa.tomcat;

import java.util.List;
import java.util.Random;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.persistence.jpa.CrudServiceBD;
import am.ajf.transaction.Transactional;

import commonj.work.Work;
import commonj.work.WorkManager;

@RequestScoped
public class SamplePolicy implements SamplePolicyBD {

	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Inject private SampleServiceBD sampleService;
	@Inject private CrudServiceBD<Model, Long> crudService;
	@Inject private UserTransaction utx;
	@Inject private EntityManager em;
	
	@Override
	public ListAllModelsRB listAllModelsCrud() {
		logger.debug("SamplePolicy : listAllModels");
		ListAllModelsRB listAllModelsRB = new ListAllModelsRB();
		//List<Model> models = sampleService.findAllModels();
		List<Model> models = crudService.find(Model.FIND_ALL);
		listAllModelsRB.setModels(models);		
		return listAllModelsRB;
	}
	
	@Override
	public ListAllModelsRB listAllModelsCustom() {
		logger.debug("SamplePolicy : listAllModels");
		ListAllModelsRB listAllModelsRB = new ListAllModelsRB();
		List<Model> models = crudService.find(Model.FIND_ALL);
		listAllModelsRB.setModels(models);		
		return listAllModelsRB;
	}

	@Override
	@Transactional
	public void createModelCustomCrud() {
		int modelId = new Random().nextInt(); 
		logger.debug("SamplePolicy : create a new Model with Crud service("+modelId+")");
		sampleService.save(new Model("model - custom crud: "+modelId));		
	}
	
	@Override
	@Transactional
	public void createModelAutoCrud() {
		int modelId = new Random().nextInt(); 
		logger.debug("SamplePolicy : create a new Model with Crud service("+modelId+")");
		crudService.save(new Model("model - auto crud: "+modelId));		
	}
	
	@Override	
	public void createModelManual() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		utx.begin();
		int modelId = new Random().nextInt(); 
		logger.debug("SamplePolicy : Manual create a new Model ("+modelId+")");
		em.persist(new Model("model - manual : "+modelId));
		utx.commit();		
	}
	
	@SuppressWarnings("unused")
	private void toDeleteMethod() throws NamingException {
		InitialContext ctx = new InitialContext();
		WorkManager mgr = (WorkManager) ctx.lookup("java:comp/env/wm/sample-jpa");
		mgr.schedule(new Work() {
			
			@Override
			public void run() {
				System.out.println("run");				
			}
			
			@Override
			public void release() {				
				System.out.println("release");
			}
			
			@Override
			public boolean isDaemon() {				
				return false;
			}
		});

	}
}
