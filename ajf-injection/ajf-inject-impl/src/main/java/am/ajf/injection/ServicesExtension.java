package am.ajf.injection;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessInjectionTarget;

import org.slf4j.Logger;

import am.ajf.core.logger.LoggerFactory;
import am.ajf.core.utils.ClassUtils;
import am.ajf.injection.ImplementationsRepository.MalformedServiceException;
import am.ajf.injection.ImplementationsRepository.NotInitializedException;
import am.ajf.injection.internal.AnnotatedTypeWrapper;
import am.ajf.injection.utils.CDIBeanFactory;

public class ServicesExtension implements Extension {

	private static final Logger logger = LoggerFactory.getLogger(ServicesExtension.class);
	private ImplementationsRepository serviceRepository;
	private ImplementationHandlersRepository serviceHandlerRepository;


	private List<Throwable> issues;
	
	public ServicesExtension() {
		super();
	}

	public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd, BeanManager beanManager) {
		logger.info("Loading AJF CDI extension 'ServicesExtension' : beginning the scanning process.");

		CDIBeanFactory.setBeanManager(beanManager);
		
		issues = new ArrayList<Throwable>();
		serviceRepository = new ImplementationsRepository();
		serviceHandlerRepository = new ImplementationHandlersRepository();		
	}
	
	public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager beanManager) {
		logger.info("Finished the scanning process.");
		serviceHandlerRepository.completeScan();
		serviceRepository.completeScan();         
		
		//for each interface get the methods and generate the subclass for each handlers.
		try {
			Set<Class<?>> interfaces = serviceRepository.getInterfaces();
			for (Class<?> in : interfaces) {
				List<Class<?>> impls = serviceRepository.getServicesForInterface(in);
				
				//easy first, no impl
				Class<?> generatedImpl = null;
				if (impls.size() == 0) {
					 //generate the impl
					 generatedImpl = serviceHandlerRepository.buildImplFor(in, null);
					 if (generatedImpl == null) {
						 throw new ClassGenerationException("Generation of implementation for interface ("+in.getName()+") failed. Is there a Handler for your methods ?");
					 }
					 serviceRepository.addService(generatedImpl);
					 //generate the bean CDI
					 AnnotatedType<?> at = beanManager.createAnnotatedType(generatedImpl); 
				     final InjectionTarget<?> it = beanManager.createInjectionTarget(at); 
					 Bean<?> bean = serviceHandlerRepository.buildBeanFor(generatedImpl, in, it);
					 abd.addBean(bean);					 
				} else { 				
					for (Class<?> impl : impls) {
						//If the implementation is not abstract, then no need to go further 
						if (Modifier.isAbstract(impl.getModifiers())) {
							generatedImpl = serviceHandlerRepository.buildImplFor(in, impl);
							if (generatedImpl == null) {
								 throw new ClassGenerationException("Generation of implementation for interface ("+in.getName()+") failed. Is there a Handler for your methods ?");
							 }
							serviceRepository.addService(generatedImpl);
							//generate the bean CDI
							AnnotatedType<?> at = beanManager.createAnnotatedType(generatedImpl); 
						    final InjectionTarget<?> it = beanManager.createInjectionTarget(at); 
							Bean<?> bean = serviceHandlerRepository.buildBeanFor(generatedImpl, in, it);
							abd.addBean(bean);	
						}
					}
				}
			}
			//serviceRepository.completeScan();
		} catch (NotInitializedException e) {
			issues.add(e);
		} catch (MalformedServiceException e) {
			issues.add(e);
		} catch (IllegalArgumentException e) {
			issues.add(e);
		} catch (SecurityException e) {
			issues.add(e);
		} catch (InstantiationException e) {
			issues.add(e);
		} catch (IllegalAccessException e) {
			issues.add(e);
		} catch (InvocationTargetException e) {
			issues.add(e);
		} catch (NoSuchMethodException e) {
			issues.add(e);
		} catch (ClassGenerationException e) {
			issues.add(e);
		}
		
		// Add the issues
		for (Throwable t : issues) {
			abd.addDefinitionError(t);
		}
		logger.info("Loaded AJF CDI extension 'ServicesExtension'.");
	}
	
	@SuppressWarnings("unchecked")
	public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat, BeanManager beanManager) {
		
		Class<T> javaClass = pat.getAnnotatedType().getJavaClass();
		logger.info("Scanning type: " + javaClass.getName());
		
		// is it a service implementation
		if (ClassUtils.isServiceImpl(javaClass)) {
			
			try {
				
				// get the corresponding service interface
				String serviceInterfaceName = ClassUtils.processServiceInterfaceName(javaClass);
				Class<?> serviceInterface = ClassUtils.loadClass(serviceInterfaceName);
				
				// get the annotated service interface
				AnnotatedType<T> interfaceAnnotatedType = (AnnotatedType<T>) beanManager.createAnnotatedType(serviceInterface);
				
				// get the service operations
				Collection<Method> serviceMethods = listMethods(interfaceAnnotatedType);

				// get the annotated service implementation
				AnnotatedType<T> annotatedType = pat.getAnnotatedType();
				
				// wrapp the service implementation  
				AnnotatedType<T> wrapped = new AnnotatedTypeWrapper<T>(
						annotatedType, serviceMethods, Monitored.class, new Monitored() {
							@Override
							public Class<? extends Annotation> annotationType() {
								return Monitored.class;
							}
						});
				
				pat.setAnnotatedType(wrapped);				
				
			} catch (ClassNotFoundException e) {
				logger.error("Error loading the interface.", e);
			}
						
		}
	
		try {
			Class<?> cus = pat.getAnnotatedType().getJavaClass();			
			if (serviceHandlerRepository.isHandler(cus)) {
				Class<ImplementationHandler> cusHandler = (Class<ImplementationHandler>) cus;
				serviceHandlerRepository.addHandler(cusHandler);
			}
			serviceRepository.addService(cus);
		} catch (MalformedServiceException e) {
			issues.add(e);
		} catch (InstantiationException e) {
			issues.add(e);
		} catch (IllegalAccessException e) {
			issues.add(e);
		}
		
	}
	
	/**
	 * 
	 * @param <T>
	 * @param annotatedType
	 * @return a list of methods
	 */
	private <T> Collection<Method> listMethods(AnnotatedType<T> annotatedType) {
		List<Method> methods = new ArrayList<Method>();
		
		Set<AnnotatedMethod<? super T>> methodsSet = annotatedType.getMethods();
		for (AnnotatedMethod<? super T> annotatedMethod : methodsSet) {
			Method method = annotatedMethod.getJavaMember();
			methods.add(method);
		}
		return methods;
	}
	
	public <T> void processInjectionTarget(@Observes ProcessInjectionTarget<T> pit, BeanManager beanManager) {
		logger.debug("process injection for: {}", pit.getAnnotatedType().getJavaClass().getName());
	}
	
	public <T> void processProcessBean(@Observes ProcessBean<T> pb, BeanManager beanManager) {
		logger.debug("process bean: {} as name {}", pb.getBean().getBeanClass().getName(), pb.getBean().getName());
	}
	
}
