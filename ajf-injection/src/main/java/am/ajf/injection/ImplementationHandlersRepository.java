package am.ajf.injection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionTarget;

import org.slf4j.Logger;

import am.ajf.core.logger.LoggerFactory;
import am.ajf.core.utils.BeanUtils;
import am.ajf.injection.api.ImplementationHandler;
import am.ajf.injection.internal.EnrichableAnnotatedTypeWrapper;
import am.ajf.injection.internal.ServiceBeanImpl;

/**
 * The services handler repository manage ServiceHandler implementation 
 * found in the classpath.
 * The main method is buildImplFor() which will use the available handlers to 
 * build the class hierarchy that extends the existing classes or implement the interface 
 * 
 * The methods to use the repository are only available when the scan is complete.
 * 
 * The repository is stateful !
 * 
 * @author Nicolas Radde (E016696)
 *
 */
public class ImplementationHandlersRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(ImplementationHandlersRepository.class);
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
	private List<ImplementationHandler> repository;
	
	
	public ImplementationHandlersRepository() {
		repositoryScanComplete = false;
		repository = new ArrayList<ImplementationHandler>();
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
	
	
	public void addHandler(Class<? extends ImplementationHandler> handler) throws InstantiationException, IllegalAccessException {	
		//add only if it implement 'ServiceHandler'
		if (ImplementationHandler.class.isAssignableFrom(handler) && !handler.isInterface()) {
			ImplementationHandler instance = BeanUtils.newInstance(handler);
			repository.add(instance);		
		}
	}
	
	public boolean isHandler(Class<?> handler) {
		return ImplementationHandler.class.isAssignableFrom(handler);
	}
		
	public List<ImplementationHandler> getHandlers() {
		return repository;
	}
	
	/**
	 * 
	 * TODO manage priority between handlers 
	 * 
	 * @param pInterface
	 * @param impl
	 * @return
	 * @throws ClassGenerationException 
	 */
	public Class<?> buildImplFor(Class<?> pInterface, Class<?> impl) throws ClassGenerationException {
		//logger.debug("Building impl for ("+pInterface.getSimpleName()+") : "+impl);
		//first create the methods to generate for each handler
		Map<ImplementationHandler, List<Method>> generators = new HashMap<ImplementationHandler, List<Method>>();		
		for (Method method : pInterface.getMethods()) {
			for (ImplementationHandler handler : repository) {
				if (handler.canHandle(method)) {
					List<Method> methodsForGenerator = generators.get(handler);
					if (methodsForGenerator == null) {
						methodsForGenerator = new ArrayList<Method>();
						methodsForGenerator.add(method);
						generators.put(handler,methodsForGenerator);						
					} else {
						methodsForGenerator.add(method);
					}	
					//only get the first capable handler for one method (see todo)
					break;
				}				
			}
		}
		
		//generate the Class for each handler and chain to the next
		//TODO manage order of handlers (TreeSet ?)
		Class<?> lastImplClass = impl;
		for (ImplementationHandler handler : generators.keySet()) {
			//logger.debug("    Handler for "+pInterface.getSimpleName()+" ("+handler.getClass().getSimpleName()+") implement : "+lastImplClass+" - "+generators.get(handler));
			lastImplClass = handler.implementMethodsFor(lastImplClass, pInterface, generators.get(handler));
			//logger.debug("      "+lastImplClass.getSimpleName() + " : "+lastImplClass.getSuperclass().getSimpleName() + " : "+lastImplClass.getInterfaces());
			//for (Method method : lastImplClass.getMethods()) {
			//	logger.debug("        "+method.getName() + " : "+Modifier.isAbstract(method.getModifiers()));
			//}
		}
		
		return lastImplClass;
	}
	
	public Bean<?> buildBeanFor(Class<?> implementation, Class<?> pInterface, InjectionTarget<?> it) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		Constructor<?> constructor = ServiceBeanImpl.class.getConstructor(InjectionTarget.class, Class.class, Class.class);
		return (Bean<?>) constructor.newInstance(it, implementation, pInterface);				 
	}
	
	/**
	 * Build the ajf annotated type for the selected cdi at.
	 * Copy the annotations on the interface to the type
	 * represented by the parameter annotated type
	 * see bug 18898
	 * 
	 * @param at
	 * @param in
	 * @return wrapped annotated type
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public AnnotatedType<?> buildAnnotatedTypeFor(AnnotatedType<?> at, Class<?> in) {
		EnrichableAnnotatedTypeWrapper<?> wat = new EnrichableAnnotatedTypeWrapper(at);
		
		//copy the class annotations
		for(Annotation an : in.getAnnotations()) {
			wat.addAnnotation(an.annotationType(), an);
		}
		
		//copy the methods annotations
		for (Method method : in.getMethods()) {
			for(Annotation an : method.getAnnotations()) {
				wat.addMethodAnnotation(method, an.annotationType(), an);
			}
		}
		
		//process
		wat.processAnnotations();
		wat.processAnnotatedMethods();
		
		return wat;
	}
	
}
