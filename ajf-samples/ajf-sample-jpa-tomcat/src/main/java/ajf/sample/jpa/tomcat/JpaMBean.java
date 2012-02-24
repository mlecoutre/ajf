package ajf.sample.jpa.tomcat;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.persistence.jpa.api.CrudBD;

@Named("jpaBean")
@RequestScoped
public class JpaMBean {
	
	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Inject private SamplePolicyBD policy;
	@Inject private CrudBD<Model, Long> crudPolicy;
	
	private List<Model> models;
	
	public JpaMBean() {
		//nothing, but needed for passivation.
	}
	
	@PostConstruct
	public void init() {
		models = new ArrayList<Model>();
	}

	public void computeModelsCustom() {
		logger.debug("JpaMBean : compute models");
		models = policy.listAllModelsCustom().getModels();
		logger.debug("models changed. found "+models.size()+" value(s)");
	}
	
	public void computeModelsServiceCrud() {
		logger.debug("JpaMBean : compute models");
		models = policy.listAllModelsCrud().getModels();
		logger.debug("models changed. found "+models.size()+" value(s)");
	}
	
	public void computeModelsPolicyCrud() {
		logger.debug("JpaMBean : compute models");
		models = crudPolicy.find(Model.FIND_ALL);
		logger.debug("models changed. found "+models.size()+" value(s)");
	}
	
	//Create possible methods
	
	public void createModelCustomServiceCrud()  {
		logger.debug("JpaMBean : creates custom service crud models");
		
		policy.createModelCustomCrud();
		
		models = policy.listAllModelsCustom().getModels();
		logger.debug("models changed. found "+models.size()+" value(s)");
	}
	
	public void createModelServiceCrud()  {
		logger.debug("JpaMBean : creates service crud models");
		
		policy.createModelAutoCrud();
		
		models = policy.listAllModelsCustom().getModels();
		logger.debug("models changed. found "+models.size()+" value(s)");
	}
	
	public void createModelPolicyCrud() throws Throwable {
		logger.debug("JpaMBean : creates policy crud models");
		
		crudPolicy.save(new Model("auto-policy created model"));
		
		models = policy.listAllModelsCustom().getModels();
		logger.debug("models changed. found "+models.size()+" value(s)");
	}
	
	public void createModelManual() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException, NamingException {
		logger.debug("JpaMBean : creates manual model");
		
		policy.createModelManual();
		
		models = policy.listAllModelsCustom().getModels();
		logger.debug("models changed. found "+models.size()+" value(s)");
	}

	public List<Model> getModels() {
		return models;
	}
}
