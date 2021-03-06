package am.ajf.injection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

import am.ajf.core.beans.BeanDefinition;
import am.ajf.core.beans.BeansManager;
import am.ajf.core.logger.LoggerFactory;
import am.ajf.core.utils.ClassUtils;
import am.ajf.core.utils.ContainerSupport;
import am.ajf.injection.ImplementationsRepository.MalformedServiceException;
import am.ajf.injection.ImplementationsRepository.NotInitializedException;
import am.ajf.injection.annotation.ErrorHandled;
import am.ajf.injection.annotation.Monitored;
import am.ajf.injection.api.ImplementationHandler;
import am.ajf.injection.internal.BeanImpl;
import am.ajf.injection.internal.ConfiguredBeanImpl;
import am.ajf.injection.internal.EJBHelper;
import am.ajf.injection.internal.EjbBeanImpl;
import am.ajf.injection.internal.EnrichableAnnotatedTypeWrapper;
import am.ajf.injection.utils.OWBBeanFactory;

public class ServicesExtension implements Extension {

	private static final Logger logger = LoggerFactory
			.getLogger(ServicesExtension.class);
	
	private ImplementationsRepository serviceRepository;
	private ImplementationHandlersRepository serviceHandlerRepository;
	
	private Set<Class<?>> ejbList;
	private Set<Class<?>> beansSet;
	
	private List<Throwable> issues;

	public ServicesExtension() {
		super();
	}

	@SuppressWarnings("unused")
	private List<Throwable> getIssues() {
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
		
		beansSet = new HashSet<Class<?>>();
		ejbList = new HashSet<Class<?>>();
		
		try {
			Set<Class<?>> declared = BeansManager.getBeans();
			if (null != declared) {
				for (Class<?> declaredBean : declared) {					
					beansSet.add(declaredBean);					
				}
			}
		} catch (Exception e) {
			issues.add(e);
			logger.error("Exception throwed while loading the non-annotated beans.", e);
		} finally {
			
		}
	}

	public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd,
			BeanManager beanManager) {
		
		logger.trace("AJF CDI extension 'ServicesExtension' : finished the scanning process.");
		
		serviceHandlerRepository.completeScan();
		serviceRepository.completeScan();
		
		// process Beans
		try {
			processBeans(beanManager, abd);
		} catch (Exception e) {
			issues.add(e);
		}	
				
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
							AnnotatedType<?> at = serviceHandlerRepository.buildAnnotatedTypeFor( 
									beanManager.createAnnotatedType(generatedImpl),
									in);
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
			
			//process ejbs
			processEJBs(beanManager, abd);
			
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
		logger.trace("AJF CDI extension 'ServicesExtension' loading done.");
	}
	
	/**
	 * Manual adding of the EJBs with the custom EJB Bean 
	 * 
	 * @param beanManager
	 * @param abd
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IllegalArgumentException 
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	private void processEJBs(BeanManager beanManager, AfterBeanDiscovery abd) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		Constructor<?> constructor = EjbBeanImpl.class.getConstructor(InjectionTarget.class, Class.class);
						
		for (Class<?> ejb : ejbList) {
			AnnotatedType<?> at = beanManager.createAnnotatedType(ejb);
			InjectionTarget<?> it = beanManager
					.createInjectionTarget(at);
						
			Bean<?> bean = (Bean<?>) constructor.newInstance(it, ejb);
			abd.addBean(bean);
		}		
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void processBeans(BeanManager beanManager, AfterBeanDiscovery abd)
			throws Exception {
		
		
		for (Class<?> beanInterface : beansSet) {
			
			try {
				
				Map<String, Set<BeanDefinition>> beanDeclarationsMap = 
						BeansManager.getBeanDefinitions(beanInterface);
				
				if ((null != beanDeclarationsMap) && (!beanDeclarationsMap.isEmpty())) {
					
					Set<Class<?>> notConfiguredBeanImplems = BeansManager.getNotConfiguredBeanImplementations(beanInterface);
					for (Class<?> beanImplemClass : notConfiguredBeanImplems) {
						AnnotatedType<?> at = beanManager
								.createAnnotatedType(beanImplemClass);
						InjectionTarget<?> it = beanManager
								.createInjectionTarget(at);
						Bean<?> bean = new BeanImpl(beanImplemClass, beanInterface, 
								at, it);
						abd.addBean(bean);
					}
					
					Set<String> keySet = beanDeclarationsMap.keySet();
					Iterator<String> keysIterator = keySet.iterator();
					while (keysIterator.hasNext()) {
						String beanProfile = keysIterator.next();
						
						BeanDefinition firstBeanDefinition = 
								beanDeclarationsMap.get(beanProfile).iterator().next();
						Class<?> beanClass = firstBeanDefinition.getBeanClass();
						
						boolean isDefaultBean = BeansManager.isDefaultBeanProfile(beanInterface, beanProfile);
						
						AnnotatedType<?> at = beanManager
								.createAnnotatedType(beanClass);
						InjectionTarget<?> it = beanManager
								.createInjectionTarget(at);
						
						Bean<?> bean = new ConfiguredBeanImpl(
								beanProfile, beanClass, isDefaultBean, firstBeanDefinition,
								at, it);
						abd.addBean(bean);
						
						if (isDefaultBean && (!BeansManager.DEFAULT_BEAN_PROFILE.equals(beanProfile))) {
							Bean<?> defaultBean = new ConfiguredBeanImpl(
									BeansManager.DEFAULT_BEAN_PROFILE, beanClass, false, firstBeanDefinition,
									at, it);
							abd.addBean(defaultBean);
						}

					}
				}
											
			} catch (Exception e) {
				issues.add(e);
			}
			
		}
	}

	@SuppressWarnings("unchecked")
	public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat,
			BeanManager beanManager) {
		
		AnnotatedType<T> annotatedType = null;
		Class<T> javaClass = null;
		try {
			annotatedType = pat.getAnnotatedType();
			javaClass = annotatedType.getJavaClass();
			logger.debug("Scanning type: " + javaClass.getName());
		} catch (Throwable e) { // for catching errors
			logger.warn("Scanning type throw exception: " + e.getClass().getName(), e);
			return;
		}
				
		if (!javaClass.isInterface()&& (!Modifier.isAbstract(javaClass.getModifiers()))) {
			
			try {
				Class<?> classInterface = findBean(javaClass);			
				if (null != classInterface) {
					try {
						Set<Class<?>> beanImplems = BeansManager.getDeclaredBeanImplementations(classInterface);
						beanImplems.add(javaClass);
						logger.trace(String.format("Register Bean implementation class '%s' for Bean '%s'.", javaClass.getName(), classInterface));
						
						// has to set veto on this Bean implementation
						Map<String, Set<BeanDefinition>> declars = BeansManager.getBeanDefinitions(classInterface);
						if ((null != declars) && (!declars.isEmpty())) {
													
							pat.veto();
							logger.info(String.format("Set VETO on class '%s', replaced by default configured one.", javaClass.getName()));
							
						}
						
					} catch (Exception e) {
						logger.warn(String.format("Exception throwed while loading Bean implementations for: %s.", classInterface), e);
					}
				}
			} catch (Exception e) {
				logger.warn("Exception throwed while registering Bean Implementation.", e);
			}
					
		}
		
		try {
			
			// annotation wrapping
			EnrichableAnnotatedTypeWrapper<T> wrapped = null;
			
			// is it a service implementation
			if (ClassUtils.isPolicyOrServiceImpl(javaClass)) {
			
				// get the corresponding service interface
				String serviceInterfaceName = ClassUtils
						.processServiceInterfaceName(javaClass);
				Class<?> serviceInterface = null;
				try {
					serviceInterface = ClassUtils
							.loadClass(serviceInterfaceName);
				} catch (Exception e) {
					// then it's not a service
				}

				if (null != serviceInterface) {
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
			}
			
			// add @ErrorHandled annotation
			boolean isPolicyImpl = ClassUtils.isPolicyImpl(javaClass);
			boolean isServiceImpl = ClassUtils.isServiceImpl(javaClass);
			boolean isWebController = ClassUtils.isWebController(javaClass);
			
			if (isPolicyImpl || isServiceImpl || isWebController) {
				
				Set<AnnotatedMethod<? super T>> componentMethods = null;
				if (isWebController) {
					
					// get the service operations
					componentMethods = new HashSet<AnnotatedMethod<? super T>>();
					
					// iterate on annotated methods
					for (AnnotatedMethod<? super T> annotatedMethod : annotatedType.getMethods()) {
						// all public methods
						if (Modifier.isPublic(annotatedMethod.getJavaMember().getModifiers())) {
							
							String methodName = annotatedMethod.getJavaMember().getName();
							if (isMBeanAction(methodName)) {
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
			issues.add(e);
		}
		
		// process EJBs class (differ processing to AfterBeanDiscovery)
		if (ContainerSupport.isEJBSupported()) {
			try {
				if (EJBHelper.addVeto(javaClass, pat)) {
					ejbList.add(javaClass);
				}
			} catch (Exception e) {
				issues.add(e);
			}
		}

		try {
			Class<?> cus = annotatedType.getJavaClass();
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
	

	@SuppressWarnings("unchecked")
	private Class<?> findBean(Class<?> javaClass) throws Exception {
		
		List<Class<?>> classInterfaces = org.apache.commons.lang.ClassUtils.getAllInterfaces(javaClass);
		if ((null != classInterfaces) && (!classInterfaces.isEmpty())) {
			for (Class<?> classInterface : classInterfaces) {
				
				if (beansSet.contains(classInterface)) {
					return classInterface;
				}
				
				if (classInterface.isAnnotationPresent(am.ajf.injection.annotation.Profile.class)) {
					beansSet.add(classInterface);
					return classInterface;
				}
											
				// also OK for AJF Services and Policies
				if (ClassUtils.isPolicyOrServiceInterface(classInterface)) {
					beansSet.add(classInterface);
					return classInterface;
				}
				
			}
		}
		
		return null;
	}

	/**
	 * 
	 * @param methodName
	 * @return true is the methodName is a MBean action
	 */
	private boolean isMBeanAction(String methodName) {
		return (!methodName.startsWith("set")) 
			|| (!methodName.startsWith("get"))
			|| (!methodName.startsWith("is"))
			|| (!methodName.equals("hashCode"))
			|| (!methodName.equals("equals"))
			|| (!methodName.equals("toString"));
	}

	public <T> void processInjectionTarget(
			@Observes ProcessInjectionTarget<T> pit, BeanManager beanManager) {

		Class<T> javaClass = pit.getAnnotatedType()
						.getJavaClass();
		logger.debug("process injection for: {}", javaClass.getName());
	
	}

	public <T> void processProcessBean(@Observes ProcessBean<T> pb,
			BeanManager beanManager) {
	
		logger.debug("process bean: {} as name {}", pb.getBean().getBeanClass()
				.getName(), pb.getBean().getName());
	}

}
