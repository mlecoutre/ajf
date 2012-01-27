package am.ajf.injection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import am.ajf.core.logger.LoggerFactory;

/**
 * The services repository manage the services interface and implementations found in
 * the application.
 * It will contain for each interface the couple :
 * (interface, implementations *)
 * The implementations will only be the last children if inheritence is used in 
 * implementations.
 * 
 * The methods to use the repository are only available when the scan is complete.
 * 
 * The repository is stateful !
 * 
 * @author Nicolas Radde (E016696)
 *
 */
public class ServiceHandlersRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceHandlersRepository.class);
	//private static ServiceHandlersRepository instance;
	
	/*
	public static ServiceHandlersRepository getInstance() {
		if (instance == null) {
			instance = new ServiceHandlersRepository();
		}
		return instance;
	}
	*/
	
	private boolean repositoryScanComplete;
	private List<ServiceHandler> repository;
	
	
	public ServiceHandlersRepository() {
		repositoryScanComplete = false;
		repository = new ArrayList<ServiceHandler>();
	}
	
	void clean() {
		repository.clear();
		repositoryScanComplete = false;		
	}
	
	public boolean isRepositoryScanComplete() {
		return repositoryScanComplete;
	}
	
	void completeScan() {
		repositoryScanComplete = true;		
		logger.info("ServiceHandler scan finished : found "+repository.size()+" Handler(s).");
	}
	
	
	public void addHandler(Class<? extends ServiceHandler> handler) throws InstantiationException, IllegalAccessException {	
		//add only if it implement 'ServiceHandler'
		if (ServiceHandler.class.isAssignableFrom(handler)) {
			ServiceHandler instance = handler.newInstance();
			repository.add(instance);		
		}
	}
	
	public boolean isHandler(Class<?> handler) {
		return ServiceHandler.class.isAssignableFrom(handler);
	}
	
	
	public List<ServiceHandler> getHandlers() {
		return repository;
	}
	
	/**
	 * 
	 * TODO manage priority between handlers 
	 * 
	 * @param pInterface
	 * @param impl
	 * @return
	 */
	public Class<?> buildImplFor(Class<?> pInterface, Class<?> impl) {
		//first create the methods to generate for each handler
		Map<ServiceHandler, List<Method>> generators = new HashMap<ServiceHandler, List<Method>>();		
		for (Method method : pInterface.getMethods()) {
			for (ServiceHandler handler : repository) {
				if (handler.canHandle(method)) {
					List<Method> methodsForGenerator = generators.get(handler);
					if (methodsForGenerator == null) {
						methodsForGenerator = new ArrayList<Method>();
						methodsForGenerator.add(method);
						generators.put(handler,methodsForGenerator);						
					} else {
						methodsForGenerator.add(method);
					}										
				}
				//only get the first capable handler for one method (see todo)
				break;
			}
		}
		
		//generate the Class for each handler and chain to the next
		//TODO manage order of handlers (TreeSet ?)
		Class<?> lastImplClass = impl;
		for (ServiceHandler handler : generators.keySet()) {
			lastImplClass = handler.implementMethodsFor(lastImplClass, generators.get(handler));			 
		}
		
		return lastImplClass;
	}
	
}
