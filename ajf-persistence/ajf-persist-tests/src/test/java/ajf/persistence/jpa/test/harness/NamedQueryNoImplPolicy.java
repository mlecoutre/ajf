package ajf.persistence.jpa.test.harness;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@RequestScoped
@Named
public class NamedQueryNoImplPolicy {

	@Inject
	private NamedQueryNoImplServiceBD service;
	
	public List<Model1> findAllModelsByName(String name) {
		return service.findAllModelsByName(name);
	}
	
}
