package ajf.persistence.jpa.test.namedquery.harness;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

@Named
public class NamedQueryWithImplService implements NamedQueryWithImplServiceBD {

	public List<Model1> findManualQuery() {
		return new ArrayList<Model1>();
	}

	public List<Model1> findAllModelsByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
