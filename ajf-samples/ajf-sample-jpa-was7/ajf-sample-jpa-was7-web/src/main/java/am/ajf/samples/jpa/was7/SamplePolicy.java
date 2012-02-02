package am.ajf.samples.jpa.was7;

import java.util.List;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.transaction.TransactionInterceptor;
import am.ajf.transaction.Transactional;

public class SamplePolicy implements SamplePolicyBD {

	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Inject private SampleServiceBD sampleService;
	@Inject @Any @Default private UserTransaction utx;
	
	
	public ListAllModelsRB listAllModels() {
		logger.debug("SamplePolicy : listAllModels");
		ListAllModelsRB listAllModelsRB = new ListAllModelsRB();
		List<Model> models = sampleService.findAllModels();
		listAllModelsRB.setModels(models);		
		return listAllModelsRB;
	}
	
	public void createModelManualTransaction(Model model) throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		utx.begin();
		logger.debug("SamplePolicy : createModel - manual transaction");		
		sampleService.save(model);
		utx.commit();
	}
	
	@Transactional
	//@Interceptors(TransactionInterceptor.class)
	public void createModelAutoTransaction(Model model) {
		logger.debug("SamplePolicy : createModel - auto transaction");		
		sampleService.save(model);				
	}
	
}
