package ajf.persistence.jpa.test.namedquery.harness;

import java.util.List;

import ajf.persistence.jpa.annotation.NamedQuery;

public interface NamedQueryWithImplServiceBD {

	@NamedQuery(name=Model1.FIND_BY_NAME)
	List<Model1> findAllModelsByName(String name);
	
	List<Model1> findManualQuery();
	
}
