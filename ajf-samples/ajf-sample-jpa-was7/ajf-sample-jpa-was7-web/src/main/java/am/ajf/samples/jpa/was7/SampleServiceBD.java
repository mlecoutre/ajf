package am.ajf.samples.jpa.was7;

import java.util.List;

import am.ajf.persistence.jpa.CrudDbService;
import am.ajf.persistence.jpa.annotation.NamedQuery;

public interface SampleServiceBD extends CrudDbService<Model, Long>{

	@NamedQuery(name=Model.FIND_ALL)
	public List<Model> findAllModels();
	
}