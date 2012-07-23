package am.ajf.web.listeners;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserSessionLifecycleListener implements HttpSessionListener {

	private Logger log = LoggerFactory.getLogger(UserSessionLifecycleListener.class);
		
	public UserSessionLifecycleListener() {
		super();
	}

	@Override
	public void sessionCreated(HttpSessionEvent se) {
				
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent evt) {
		String sessionId = evt.getSession().getId();
		log.info(String.format("Session #%s invalidated.", sessionId));
	}

}
