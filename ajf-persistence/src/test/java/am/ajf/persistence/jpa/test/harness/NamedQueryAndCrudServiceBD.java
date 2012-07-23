package am.ajf.persistence.jpa.test.harness;

import java.util.List;

import am.ajf.persistence.jpa.annotation.NamedQuery;
import am.ajf.persistence.jpa.annotation.QueryParam;
import am.ajf.persistence.jpa.api.CrudServiceBD;

public interface NamedQueryAndCrudServiceBD 
	extends CrudServiceBD<Model1, Long>{

	@NamedQuery(name=Model1.FIND_BY_NAME)
	List<Model1> findAllModelsByName(
			@QueryParam("name") String name);
	
}
