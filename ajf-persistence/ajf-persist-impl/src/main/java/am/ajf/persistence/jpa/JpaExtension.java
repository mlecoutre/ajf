package am.ajf.persistence.jpa;

//import java.lang.annotation.Annotation;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.persistence.jpa.impl.EntityManagerBean;

public class JpaExtension implements Extension {
	
	private static final Logger logger = LoggerFactory.getLogger(JpaExtension.class);
	
	public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
		
		logger.debug("beginning the scanning process for extension JpaExtension");
		/*
		//FIXME since injection doesnt seem to work on the extension object...
		implementationGenerator = new JavaassistImplementationGenerator();
		classMatcher = new ExtensionClassMatcher();
		*/
	}

	public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
		/*
		Class<T> javaClass = pat.getAnnotatedType().getJavaClass();
		logger.debug("scanning type: "
				+ javaClass.getName());
		//init the dependency tree
		if (haveAnImplementation == null) {
			haveAnImplementation = new HashMap<Class<?>, Boolean>();
		}	
		if (interfaceImplementationMatch == null) {
			interfaceImplementationMatch = new HashMap<Class<?>, List<Class<?>>>();
		}
		
		//process the interfaces
		//init the interface at false if it doesnt exist (the interface was scanned before the impl)
		if (classMatcher.isServiceInterface(javaClass)) {
			logger.debug("adding classloader for : "+javaClass);
			//TODO application classes and extension classes might not be on same class loader (Tomcat, WAS)
			implementationGenerator.addClasspathFor(javaClass);						
			if (!haveAnImplementation.containsKey(javaClass)) {
				haveAnImplementation.put(javaClass, false);				
			}
		}
		//process the implementations
		//for each impl found, set the interface as having an impl.
		//for each impl found, add the impl to the list of possible impl for the interface.
		if (classMatcher.isServiceClass(javaClass)) {
			for (Class<?> in : javaClass.getInterfaces()) {
				if (in.getName().endsWith("ServiceBD")) {					
					haveAnImplementation.put(in, true);
					if (interfaceImplementationMatch.get(in) == null) {
						interfaceImplementationMatch.put(in, new ArrayList<Class<?>>());
					}
					List<Class<?>> iim = interfaceImplementationMatch.get(in);
					iim.add(javaClass);
				}
			}
		}
		*/
	}
	
	public <T> void ProcessInjectionTarget(@Observes ProcessInjectionTarget<T> pit) {
		/*
		logger.debug("			injection target: "
				+ pit.getAnnotatedType().getJavaClass().getName());
				*/
	}
	
	public <T> void processProcessBean(@Observes ProcessBean<T> pb) {
		/*logger.debug("			bean : "
				+ pb.getBean().getBeanClass().getName());
				*/
	}

	public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager beanManager) throws CannotCompileException, NotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, NoSuchFieldException, ClassNotFoundException {
		logger.debug("After Bean discovery : JPA Extension");
		Set<String> puNames = EntityManagerProvider.getPersistenceUnitNames();
		//AnnotatedType<EntityManager> at = beanManager.createAnnotatedType(EntityManager.class);
		//final InjectionTarget<EntityManager> it = beanManager.createInjectionTarget(at);
		for (String puName : puNames) {
			EntityManagerBean emBean = new EntityManagerBean(puName);
			abd.addBean(emBean);
			logger.info("Added injectable EntityManager for : "+puName);
		}		
	}
	
	
	

}
