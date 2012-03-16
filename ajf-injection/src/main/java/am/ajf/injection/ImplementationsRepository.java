package am.ajf.injection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class ImplementationsRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(ImplementationsRepository.class);
	//private static ServicesRepository instance;
	
	/*
	public static ServicesRepository getInstance() {
		if (instance == null) {
			instance = new ServicesRepository();
		}
		return instance;
	}
	*/
	
	private boolean repositoryScanComplete;
	private Map<Class<?>, List<Class<?>>> repository;
	
	
	public ImplementationsRepository() {
		repositoryScanComplete = false;
		repository = new HashMap<Class<?>, List<Class<?>>>();
	}
	
	void clean() {
		repository.clear();
		repositoryScanComplete = false;		
	}
	
	public boolean isRepositoryScanComplete() {
		return repositoryScanComplete;
	}
	
	void completeScan() {
		logger.debug("Dumping the AJF services found :");
		repositoryScanComplete = true;
		int nbInterfaces = repository.size();
		int nbImpls = 0;
		for (Class<?> in : repository.keySet()) {
			StringBuffer sb = new StringBuffer();
			boolean isFirst = true;
			sb.append("[");
			for (Class<?> impl : repository.get(in)) {
				if (!isFirst) {
					sb.append(",");
				}
				sb.append(impl.getSimpleName());				
				isFirst = false;
			}
			sb.append("]");
			logger.debug("  " + in.getSimpleName() + " : "+sb.toString());
			nbImpls += repository.get(in).size();
		}
		logger.info("Services scan finished : found "+nbInterfaces+" interface(s) and "+nbImpls+" implementation(s).");
	}
	
	
	public boolean addService(Class<?> service) throws MalformedServiceException {	
		//first test the interface case
		if (isServiceInterface(service)) {
			//dont add it if the interface was already added
			if (!repository.containsKey(service)) {
				repository.put(service, new ArrayList<Class<?>>());
			}
		} else if (isService(service)) {
			//get the BD interface for the service, there should be only one
			Class<?>[] interfaces = service.getInterfaces();
			Class<?> vInterface = null;
			for (Class<?> in : interfaces) {
				if (isServiceInterface(in)) {
					vInterface = in;
					break;
				}
			}
			if (vInterface == null) {
				//This is not a service
				return false;
				//throw new MalformedServiceException(service);
			}
							
			//Add the service and/or interface to the repo
			
			List<Class<?>> impls = repository.get(vInterface);				
			//The first case, no interface was already scanned
			if (impls == null) {
				List<Class<?>> tmp = new ArrayList<Class<?>>();
				tmp.add(service);
				repository.put(vInterface, tmp);
			//The second simple case, the interface was already scanned, but no other impls
			} else if (impls.size() == 0) {
				impls.add(service);
			//Here come the pain
			} else {
				//TODO need to manage the cases when there is inheritence between services on the client side
				boolean vetoAdd = false;
				for (Class<?> clazz : impls) {
					//remove the existing if we are about to add a subclass of an existing impl
					if (clazz.isAssignableFrom(service)) {
						impls.remove(clazz);
						break;
					}
					//if we are about to add a parent, then veto the add
					if (service.isAssignableFrom(clazz)) {
						vetoAdd = true;
						break;
					}
				}
				if (!vetoAdd) {
					impls.add(service);
				}
			}				
		} else {
			//nothing, you are trying to add a non service class
		}
		
		return true;
		
	}
	
	public List<Class<?>> getServicesForInterface(Class<?> pInterface) throws NotInitializedException{
		if (isRepositoryScanComplete()) {
			return repository.get(pInterface);
		} else {
			throw new NotInitializedException();
		}	
	}
	
	public Set<Class<?>> getInterfaces() throws NotInitializedException{
		if (isRepositoryScanComplete()) {
			return repository.keySet();
		} else {
			throw new NotInitializedException();
		}	
	}
	
	public boolean isImplemented(Class<?> pInterface) throws NotInitializedException {
		if (isRepositoryScanComplete()) {
			List<Class<?>> impls = repository.get(pInterface);
			if (impls == null) {
				return false;
			} else if (impls.size() == 0) {
				return false;
			} else {
				return true;
			}
		} else {
			throw new NotInitializedException();
		}
	}
	
	public boolean isService(Class<?> service) {
		boolean res = false;
		if (service.getName().endsWith("Service")) {
			res = true;
		}
		return res;
	}
	
	public boolean isServiceInterface(Class<?> in) {
		boolean res = false;
		if (in.getName().endsWith("ServiceBD") && in.isInterface()) {
			res = true;
		}
		return res;
	}
	
	public class NotInitializedException extends Exception {
		private static final long serialVersionUID = -8614276303766577344L;
		public NotInitializedException() {super();}
	}
	public class AlreadyInitializedException extends Exception {
		private static final long serialVersionUID = 1600140302809274093L;
		public AlreadyInitializedException() {super();}
	}
	public class MalformedServiceException extends Exception {
		private static final long serialVersionUID = 5600143302809274093L;
		private Class<?> service;
		public MalformedServiceException(Class<?> service) {
			super("Malformed service : "+service.getName() + "\n" + service.toString());
			this.service = service;
		}
		
		public Class<?> getService()  {
			return service;
		}
	}
}
