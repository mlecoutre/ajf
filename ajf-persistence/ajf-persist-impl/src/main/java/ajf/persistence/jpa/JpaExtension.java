package ajf.persistence.jpa;

//import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.NotFoundException;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ajf.persistence.jpa.impl.ExtensionClassMatcher;
import ajf.persistence.jpa.impl.JavaassistImplementationGenerator;

public class JpaExtension implements Extension {

	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Class<?>, Boolean> haveAnImplementation;
	public static java.lang.annotation.Annotation ANNOTATION_DEFAULT = new AnnotationLiteral<Default>() {};
	public static java.lang.annotation.Annotation ANNOTATION_ANY = new AnnotationLiteral<Any>() {};
	
	 @Inject private ImplementationGenerator implementationGenerator;
	 @Inject private ClassMatcher classMatcher;
	
	public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {		
		logger.debug("beginning the scanning process for extension JpaExtension");
		//FIXME since injection doesnt seem to work on the extension object...
		implementationGenerator = new JavaassistImplementationGenerator();
		classMatcher = new ExtensionClassMatcher();
	}

	public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {		
		Class<T> javaClass = pat.getAnnotatedType().getJavaClass();
		logger.debug("scanning type: "
				+ javaClass.getName());
		//init the dependency tree
		if (haveAnImplementation == null) {
			haveAnImplementation = new HashMap<Class<?>, Boolean>();
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
		if (classMatcher.isServiceClass(javaClass)) {
			for (Class<?> in : javaClass.getInterfaces()) {
				if (in.getName().endsWith("ServiceBD")) {					
					haveAnImplementation.put(in, true);
				}
			}
		}
	}
	
	public <T> void ProcessInjectionTarget(@Observes ProcessInjectionTarget<T> pit) {
		logger.debug("			injection target: "
				+ pit.getAnnotatedType().getJavaClass().getName());
	}
	
	public <T> void processProcessBean(@Observes ProcessBean<T> pb) {
		logger.debug("			bean : "
				+ pb.getBean().getBeanClass().getName());
	}

	public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager beanManager) throws CannotCompileException, NotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, NoSuchFieldException, ClassNotFoundException {
		logger.debug("finished the scanning process for extension JpaExtension, printing all beans found :");
		Set<Bean<?>> allBeans = beanManager.getBeans(Object.class, new AnnotationLiteral<Any>() {});
		for (Bean<?> bean : allBeans) {
			logger.debug("   bean : " + bean.getBeanClass().getName());
		}
		logger.debug("dump the dep graph");
		
		for (Class<?> in : haveAnImplementation.keySet()) {
			logger.debug(in.getName() + " : "+haveAnImplementation.get(in));
			if (!haveAnImplementation.get(in)) {
				Class<?> impl = implementationGenerator.createImpl(in);
				 //use this to read annotations of the class
		        AnnotatedType<?> at = beanManager.createAnnotatedType(impl); 
		        //use this to instantiate the class and inject dependencies
		        final InjectionTarget<?> it = beanManager.createInjectionTarget(at);	
		        abd.addBean(implementationGenerator.createBeanFromImpl(impl,in, it));				
			}
		}
		
	}
	
	
	

}
