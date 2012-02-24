package am.ajf.persistence.jpa;

import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessInjectionTarget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.persistence.jpa.impl.EntityManagerBean;

public class JpaExtension implements Extension {
	
	private static final Logger logger = LoggerFactory.getLogger(JpaExtension.class);
	
	public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
	
	}

	public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
		
	}
	
	public <T> void ProcessInjectionTarget(@Observes ProcessInjectionTarget<T> pit) {
		
	}
	
	public <T> void processProcessBean(@Observes ProcessBean<T> pb) {		
	}

	public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager beanManager) {
		Set<String> puNames = EntityManagerProvider.getPersistenceUnitNames();		
		for (String puName : puNames) {
			EntityManagerBean emBean = new EntityManagerBean(puName);
			abd.addBean(emBean);
			logger.info("Added injectable EntityManager for : "+puName);
		}		
	}
	
	
	

}
