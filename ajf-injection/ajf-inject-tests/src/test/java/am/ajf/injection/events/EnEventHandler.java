package am.ajf.injection.events;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

public class EnEventHandler {
	
	@Inject
	private Logger logger;

	public EnEventHandler() {
		super();
	}

	public void handleEvent(@Observes Event event) {
		logger.info("Receive and event: ".concat(event.toString()));
	}
	
}
