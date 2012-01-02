package ajf.persistence.jpa.test.harness;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

@Named
public abstract class NamedQueryWithImplService implements NamedQueryWithImplServiceBD {

	@Override
	public List<Model1> findManualQuery() {
		return new ArrayList<Model1>();
	}
	
}
