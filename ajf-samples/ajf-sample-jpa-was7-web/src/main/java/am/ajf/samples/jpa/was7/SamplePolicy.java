package am.ajf.samples.jpa.was7;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SamplePolicy implements SamplePolicyBD {

	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Inject private SampleServiceBD sampleService;
	
	
	public ListAllModelsRB listAllModels() {
		logger.debug("SamplePolicy : listAllModels");
		ListAllModelsRB listAllModelsRB = new ListAllModelsRB();
		List<Model> models = sampleService.findAllModels();
		listAllModelsRB.setModels(models);		
		return listAllModelsRB;
	}
	
}
