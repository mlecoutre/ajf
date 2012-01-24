package am.ajf.injection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessInjectionTarget;

import org.slf4j.Logger;

import am.ajf.core.logger.LoggerFactory;
import am.ajf.core.utils.ClassUtils;
import am.ajf.injection.utils.CDIBeanFactory;

public class ServicesExtension implements Extension {

	// private final Logger logger = LoggerFactory.getLogger(ServicesExtension.class);
	
	// private List<Class<?>> servicesList = null;
	
	@SuppressWarnings("unused")
	private Map<Class<?>, List<Class<?>>> interfaceImplementationsMatch = null;
	
	public ServicesExtension() {
		super();
	}

	public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd, BeanManager beanManager) {
		CDIBeanFactory.setBeanManager(beanManager);
		/*
		logger.info("beforeBeanDiscovery, beginning the scanning process for extension 'ServicesExtension'.");
		servicesList = new ArrayList<Class<?>>();
		interfaceImplementationsMatch = new HashMap<Class<?>, List<Class<?>>>();
		*/
	}
	
	public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager beanManager) {
		// logger.info("afterBeanDiscovery, finished the scanning process for extension 'ServicesExtension'");
	}
	
	public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat, BeanManager beanManager) {
		/*
		logger.debug("\tprocessAnnotatedType: "
				+ pat.getAnnotatedType().getJavaClass().getName());
		
		Class<T> javaClass = pat.getAnnotatedType().getJavaClass();
		if (ClassUtils.isService(javaClass)) {
			servicesList.add(javaClass);
			logger.info("\tfind service interface: "
					+ pat.getAnnotatedType().getJavaClass().getName());
		}
		*/
	}
	
	public <T> void processInjectionTarget(@Observes ProcessInjectionTarget<T> pit) {
		/*
		logger.debug("\tprocessInjectionTarge target: "
				+ pit.getAnnotatedType().getJavaClass().getName());
		*/
	}
	
	public <T> void processProcessBean(@Observes ProcessBean<T> pb) {
		/*
		logger.debug("\tprocessProcessBean : "
				+ pb.getBean().getBeanClass().getName());
		*/
	}
	
}
