package am.ajf.injection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessInjectionTarget;

import org.slf4j.Logger;

import am.ajf.core.logger.LoggerFactory;
import am.ajf.core.utils.ClassUtils;
import am.ajf.injection.internal.AnnotatedTypeWrapper;
import am.ajf.injection.utils.CDIBeanFactory;

public class ServicesExtension implements Extension {

	private final Logger logger = LoggerFactory.getLogger(ServicesExtension.class);
		
	@SuppressWarnings("unused")
	private Map<Class<?>, List<Class<?>>> interfaceImplementationsMatch = null;
	
	public ServicesExtension() {
		super();
	}

	public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd, BeanManager beanManager) {
		
		logger.info("beginning the scanning process.");
		CDIBeanFactory.setBeanManager(beanManager);
	
	}
	
	public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager beanManager) {
		logger.info("finished the scanning process.");
	}
	
	@SuppressWarnings("unchecked")
	public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat, BeanManager beanManager) {
		
		Class<T> javaClass = pat.getAnnotatedType().getJavaClass();
		logger.info("scanning type: " + javaClass.getName());
		
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
				
			}
			catch (ClassNotFoundException e) {
				logger.error(e.getMessage());
			}
						
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
		/*
		logger.debug("process bean: {} as name {}", pb.getBean().getBeanClass().getName(), pb.getBean().getName());
		*/
	}
	
}
