package am.ajf.monitoring;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import am.ajf.core.logger.LoggerFactory;

public class EventsDomainManager {

	@SuppressWarnings("unused")
	private final static Logger logger = LoggerFactory
			.getLogger(EventsDomainManager.class);

	private static EventsDomain defaultEventsDomain = null;
	private static Map<String, EventsDomain> eventsDomainsMap = new ConcurrentHashMap<String, EventsDomain>();

	static {
		initClass();
	}

	/**
	 * init impls map
	 */
	private static void initClass() {

		defaultEventsDomain = new EventsDomain();

	}

	private EventsDomainManager() {
		super();
	}

	/**
	 * return the default EventsDomain
	 * 
	 * @return default EventsDomain instance
	 * @throws NullPointerException
	 */
	public static EventsDomain getEventsDomain() throws NullPointerException {

		return defaultEventsDomain;
	}

	/**
	 * return a nammed EventsDomain instance
	 * 
	 * @param eventsDomainName
	 * @return an EventsDomain instance
	 * @throws NullPointerException
	 */
	public static EventsDomain getEventsDomain(String eventsDomainName)
			throws NullPointerException {

		EventsDomain eventsDomain = null;
		if (!eventsDomainsMap.containsKey(eventsDomainName)) {
			createEventsDomain(eventsDomainName);
		}

		eventsDomain = eventsDomainsMap.get(eventsDomainName);
		return eventsDomain;
	}

	protected static synchronized void createEventsDomain(
			String eventsDomainName) {
		
		if (eventsDomainsMap.containsKey(eventsDomainName))
			return;
		
		EventsDomain eventManager = new EventsDomain();
		eventsDomainsMap.put(eventsDomainName, eventManager);
	}

	/**
	 * return the EventManager names
	 * 
	 * @return
	 */
	public static Collection<String> getEventsDomainNames() {
		return eventsDomainsMap.keySet();
	}

}
