package foo.core.services;

import javax.inject.Inject;

import org.slf4j.Logger;

public abstract class AbstractService {

	@Inject
	protected Logger logger;
	
	public AbstractService() {
		super();
	}

}
