package am.ajf.injection;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
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
import am.ajf.injection.internal.EnrichableAnnotatedTypeWrapper;
import am.ajf.injection.utils.OWBBeanFactory;

public class ServicesExtension implements Extension {

	private static final Logger logger = LoggerFactory
			.getLogger(ServicesExtension.class);
	private ImplementationsRepository serviceRepository;
	private ImplementationHandlersRepository serviceHandlerRepository;

	private List<Throwable> issues;

	public ServicesExtension() {
		super();
	}

	public List<Throwable> getIssues() {
		return issues;
	}

	public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd,
			BeanManager beanManager) {
		
		logger.trace("AJF CDI extension 'ServicesExtension' : beginning the scanning process.");

		// attache the beanManager to OWBBeanFactory
		OWBBeanFactory.setBeanManager(beanManager);

		issues = new ArrayList<Throwable>();
		serviceRepository = new ImplementationsRepository();
		serviceHandlerRepository = new ImplementationHandlersRepository();
	}

	public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd,
			BeanManager beanManager) {
		
		logger.trace("AJF CDI extension 'ServicesExtension' : finished the scanning process.");
		
		serviceHandlerRepository.completeScan();
		serviceRepository.completeScan();

		// for each interface get the methods and generate the subclass for each
		// handlers.
		try {
			Set<Class<?>> interfaces = serviceRepository.getInterfaces();
			for (Class<?> in : interfaces) {
				List<Class<?>> impls = serviceRepository
						.getServicesForInterface(in);

				// easy first, no impl
				Class<?> generatedImpl = null;
				if (impls.size() == 0) {
					// generate the impl
					generatedImpl = serviceHandlerRepository.buildImplFor(in,
							null);
					if (generatedImpl == null) {
						throw new ClassGenerationException(
								"Generation of implementation for interface ("
										+ in.getName()
										+ ") failed. Is there a Handler for your methods ?");
					}
					serviceRepository.addService(generatedImpl);
					// generate the bean CDI
					AnnotatedType<?> at = beanManager
							.createAnnotatedType(generatedImpl);
					final InjectionTarget<?> it = beanManager
							.createInjectionTarget(at);
					Bean<?> bean = serviceHandlerRepository.buildBeanFor(
							generatedImpl, in, it);
					abd.addBean(bean);
				} else {
					for (Class<?> impl : impls) {
						// If the implementation is not abstract, then no need
						// to go further
						if (Modifier.isAbstract(impl.getModifiers())) {
							generatedImpl = serviceHandlerRepository
									.buildImplFor(in, impl);
							if (generatedImpl == null) {
								throw new ClassGenerationException(
										"Generation of implementation for interface ("
												+ in.getName()
												+ ") failed. Is there a Handler for your methods ?");
							}
							serviceRepository.addService(generatedImpl);
							// generate the bean CDI
							AnnotatedType<?> at = beanManager
									.createAnnotatedType(generatedImpl);
							final InjectionTarget<?> it = beanManager
									.createInjectionTarget(at);
							Bean<?> bean = serviceHandlerRepository
									.buildBeanFor(generatedImpl, in, it);
							abd.addBean(bean);
						}
					}
				}
			}
			// serviceRepository.completeScan();
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
		logger.trace("AJF CDI extension 'ServicesExtension'.");
	}

	@SuppressWarnings("unchecked")
	public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat,
			BeanManager beanManager) {

		Class<T> javaClass = pat.getAnnotatedType().getJavaClass();
		logger.debug("Scanning type: " + javaClass.getName());
		
		try {
			
			// annotation wrapping
			EnrichableAnnotatedTypeWrapper<T> wrapped = null;
			
			// is it a service implementation
			if (ClassUtils.isPolicyOrServiceImpl(javaClass)) {
			
				// get the corresponding service interface
				String serviceInterfaceName = ClassUtils
						.processServiceInterfaceName(javaClass);
				Class<?> serviceInterface = ClassUtils
						.loadClass(serviceInterfaceName);

				// get the annotated service interface
				AnnotatedType<T> interfaceAnnotatedType = (AnnotatedType<T>) beanManager
						.createAnnotatedType(serviceInterface);
				// get the service operations
				Set<AnnotatedMethod<? super T>> serviceMethods = interfaceAnnotatedType
						.getMethods();

				// enrich the methods annotations
				if ((null != serviceMethods) && !serviceMethods.isEmpty()) {
					
					// create the annotated type wrapper if not exist
					if (null == wrapped) {
						// get the annotated service implementation
						AnnotatedType<T> annotatedType = pat.getAnnotatedType();
						// wrapp the service implementation
						wrapped = new EnrichableAnnotatedTypeWrapper<T>(
							annotatedType);
					}

					// add monitored annotation for each overrided method
					Class<? extends Annotation> annotationType = Monitored.class;
					Monitored annotation = new Monitored() {
						@Override
						public Class<? extends Annotation> annotationType() {
							return Monitored.class;
						}
					};
					
					// add the new annotation
					for (AnnotatedMethod<? super T> svcMethod : serviceMethods) {
						wrapped.addMethodAnnotation(svcMethod.getJavaMember(), annotationType, annotation);					
					}
				}

			}
			
			// add @ErrorHandled annotation
			boolean isPolicyImpl = ClassUtils.isPolicyImpl(javaClass);
			boolean isServiceImpl = ClassUtils.isServiceImpl(javaClass);
			boolean isWebController = ClassUtils.isWebController(javaClass);
			
			if (isPolicyImpl || isServiceImpl || isWebController) {
				
				Set<AnnotatedMethod<? super T>> componentMethods = null;
				if (isWebController) {
					
					// get the annotated service implementation
					AnnotatedType<T> annotatedType = pat.getAnnotatedType();
					
					// get the service operations
					componentMethods = new HashSet<AnnotatedMethod<? super T>>();
					
					// iterate on annotated methods
					for (AnnotatedMethod<? super T> annotatedMethod : annotatedType.getMethods()) {
						// all public methods
						if (Modifier.isPublic(annotatedMethod.getJavaMember().getModifiers())) {
							
							String methodName = annotatedMethod.getJavaMember().getName();
							if ((!methodName.startsWith("set")) 
								|| (!methodName.startsWith("get"))
								|| (!methodName.startsWith("is"))
								|| (!methodName.equals("hashCode"))
								|| (!methodName.equals("equals"))
								|| (!methodName.equals("toString"))
								) {
								
								componentMethods.add(annotatedMethod);
								
							}
								
						}
					}
					
				}
				else {
				
					// get the corresponding service interface
					String serviceInterfaceName = ClassUtils
							.processServiceInterfaceName(javaClass);
					Class<?> serviceInterface = ClassUtils
							.loadClass(serviceInterfaceName);
	
					// get the annotated service interface
					AnnotatedType<T> interfaceAnnotatedType = (AnnotatedType<T>) beanManager
							.createAnnotatedType(serviceInterface);
					// get the service operations
					componentMethods = interfaceAnnotatedType
							.getMethods();
				}
					
				// enrich the methods annotations
				if ((null != componentMethods) && !componentMethods.isEmpty()) {
					
					// create the annotated type wrapper if not exist
					if (null == wrapped) {
						// get the annotated service implementation
						AnnotatedType<T> annotatedType = pat.getAnnotatedType();
						// wrapp the service implementation
						wrapped = new EnrichableAnnotatedTypeWrapper<T>(
							annotatedType);
					}
					
					// add errorHandled annotation for each overrided method
					Class<? extends Annotation> annotationType = ErrorHandled.class;
					ErrorHandled annotation = new ErrorHandled() {
						@Override
						public Class<? extends Annotation> annotationType() {
							return ErrorHandled.class;
						}
					};
					
					// add the new annotation
					for (AnnotatedMethod<? super T> svcMethod : componentMethods) {
						wrapped.addMethodAnnotation(svcMethod.getJavaMember(), annotationType, annotation);					
					}
				}
			}
			
			
			if (null != wrapped) {
				
				// process the annotatedMethods
				wrapped.processAnnotatedMethods();
				// process the type annotations
				wrapped.processAnnotations();

				// register the new annotated type
				pat.setAnnotatedType(wrapped);
				
			}
			
		} catch (ClassNotFoundException e) {
			logger.error("Error loading the interface.", e);
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

	public <T> void processInjectionTarget(
			@Observes ProcessInjectionTarget<T> pit, BeanManager beanManager) {
		logger.debug("process injection for: {}", pit.getAnnotatedType()
				.getJavaClass().getName());
	}

	public <T> void processProcessBean(@Observes ProcessBean<T> pb,
			BeanManager beanManager) {
		logger.debug("process bean: {} as name {}", pb.getBean().getBeanClass()
				.getName(), pb.getBean().getName());
	}

}
