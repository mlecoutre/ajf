package am.ajf.samples.jpa.was7;

import java.util.List;

import am.ajf.persistence.jpa.CrudServiceBD;
import am.ajf.persistence.jpa.annotation.NamedQuery;

public interface SampleServiceBD extends CrudServiceBD<Model, Long>{

	@NamedQuery(name=Model.FIND_ALL)
	public List<Model> findAllModels();
	
}
