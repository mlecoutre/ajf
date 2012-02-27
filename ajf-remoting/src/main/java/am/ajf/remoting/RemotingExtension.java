package am.ajf.remoting;


import javassist.CannotCompileException;
import javassist.NotFoundException;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessInjectionTarget;

public class RemotingExtension implements Extension {
	
	public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {		
		//Nothing for now
	}

	public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {		
		//Nothing for now
	}
	
	public <T> void ProcessInjectionTarget(@Observes ProcessInjectionTarget<T> pit) {
		//Nothing for now
	}
	
	public <T> void processProcessBean(@Observes ProcessBean<T> pb) {
		//Nothing for now
	}

	public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager beanManager) throws CannotCompileException, NotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, NoSuchFieldException, ClassNotFoundException {
		//Nothing for now		
	}
}
