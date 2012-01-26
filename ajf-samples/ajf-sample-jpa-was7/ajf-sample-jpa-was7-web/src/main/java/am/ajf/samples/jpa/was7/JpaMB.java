package am.ajf.samples.jpa.was7;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named(value="jpaBean")
@ApplicationScoped
public class JpaMB implements Serializable {
	
	private static final long serialVersionUID = 1687667378623102828L;
	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Inject private SamplePolicyBD policy;
	//@EJB private SampleEJBPolicyBD ejbPolicy;
	
	private List<Model> models;
	
	public JpaMB() {
		//nothing, but needed for passivation.
	}
	
	@PostConstruct
	public void init() {
		models = new ArrayList<Model>();
	}

	public void computeModelsByPolicy(ActionEvent event) {
		logger.debug("JpaMBean : compute models by Policy");
		models = policy.listAllModels().getModels();
		logger.debug("models changed. found "+models.size()+" value(s)");
	}
	
	public void computeModelsByEJB(ActionEvent event) {
		logger.debug("JpaMBean : compute models by EJB");
		//models = ejbPolicy.listAllModels().getModels();
		logger.debug("models changed. found "+models.size()+" value(s)");
	}
	
	public void createModelManualTransactionByPolicy(ActionEvent event) throws SecurityException, IllegalStateException, NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		logger.debug("JpaMBean : creates models by Policy - manual transaction");
		
		policy.createModelManualTransaction(new Model("model policy - transaction manual: "+new Random().nextInt()));
		
		models = policy.listAllModels().getModels();
		logger.debug("models changed. found "+models.size()+" value(s)");
	}
	
	public void createModelAutoTransactionByPolicy(ActionEvent event) {
		logger.debug("JpaMBean : creates models by Policy - auto transaction");		
		
		policy.createModelAutoTransaction(new Model("model policy - transaction auto: "+new Random().nextInt()));
		
		models = policy.listAllModels().getModels();
		logger.debug("models changed. found "+models.size()+" value(s)");
	}
	
	public void createModelByEJB(ActionEvent event) {
		logger.debug("JpaMBean : creates models by EJB");
		
		//ejbPolicy.createModel(new Model("model ejb policy : "+new Random().nextInt()));
		
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
