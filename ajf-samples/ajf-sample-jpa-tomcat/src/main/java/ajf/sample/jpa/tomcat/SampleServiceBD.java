package ajf.sample.jpa.tomcat;

import java.util.List;

import am.ajf.persistence.jpa.annotation.NamedQuery;
import am.ajf.persistence.jpa.api.CrudServiceBD;

public interface SampleServiceBD extends CrudServiceBD<Model, Long> {

	@NamedQuery(name=Model.FIND_ALL)
	public List<Model> findAllModels();
	
}
