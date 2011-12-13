package ajf.sample.jpa.tomcat;

import java.util.List;

import ajf.persistence.jpa.annotation.NamedQuery;

public interface SampleServiceBD {

	@NamedQuery(name=Model.FIND_ALL)
	public List<Model> findAllModels();
	
}
