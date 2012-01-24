package ajf.sample.jpa.tomcat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
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
	
	public void createModelCrud(ActionEvent event)  {
		logger.debug("JpaMBean : creates crud models");
		
		policy.createModelCrud();
		
		models = policy.listAllModels().getModels();
		logger.debug("models changed. found "+models.size()+" value(s)");
	}
	
	public void createModelManual(ActionEvent event) throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		logger.debug("JpaMBean : creates manual model");
		
		policy.createModelManual();
		
		models = policy.listAllModels().getModels();
		logger.debug("models changed. found "+models.size()+" value(s)");
	}

	public List<Model> getModels() {
		return models;
	}
}
