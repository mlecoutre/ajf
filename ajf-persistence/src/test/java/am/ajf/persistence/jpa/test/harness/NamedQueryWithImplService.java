package am.ajf.persistence.jpa.test.harness;

import java.util.ArrayList;
import java.util.List;

public abstract class NamedQueryWithImplService implements NamedQueryWithImplServiceBD {

	@Override
	public List<Model1> findManualQuery() {
		return new ArrayList<Model1>();
	}
	
}
