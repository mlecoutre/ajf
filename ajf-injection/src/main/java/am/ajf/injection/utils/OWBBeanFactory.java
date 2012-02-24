package am.ajf.injection.utils;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ContainerLifecycle;
import org.slf4j.Logger;

import com.google.common.base.Throwables;

import am.ajf.core.logger.LoggerFactory;
import am.ajf.core.utils.BeanFactory;

public class OWBBeanFactory 
	implements BeanFactory {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static ContainerLifecycle container;
	private static BeanManager beanManager;
	
	/**
	 * Default constructor 
	 */
	public OWBBeanFactory() {
		super();
		init();
	}
	
	public static ContainerLifecycle getContainer() {
		return container;
	}

	public static void setContainer(ContainerLifecycle container) {
		OWBBeanFactory.container = container;
	}

	public static BeanManager getBeanManager() {
		return beanManager;
	}

	public static void setBeanManager(BeanManager beanManager) {
		OWBBeanFactory.beanManager = beanManager;
	}

	/*
	 * 
	 */
	public synchronized void init() {
		
		if (null != beanManager)
			return;
		
		WebBeansContext beansContext = WebBeansContext.currentInstance();
		container = beansContext.getService(ContainerLifecycle.class);
		container.startApplication(null);
		
		beanManager = container.getBeanManager();
		
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T instanciate(Class<T> beanClass) throws RuntimeException {
		try {
		
			CreationalContext<T> cCtx = (CreationalContext<T>) beanManager.createCreationalContext(null);
			
			T result = null;
			
			
			if (beanClass.isInterface()) {
				Set<Bean<?>> beans = beanManager.getBeans(beanClass);
				Bean<?> bean = beans.iterator().next();
				result = (T) beanManager.getReference(bean, beanClass, cCtx);
			}
			else {
				AnnotatedType<T> type = beanManager.createAnnotatedType(beanClass);
				InjectionTarget<T> it = beanManager.createInjectionTarget(type);
				result = it.produce(cCtx);
				it.inject(result, cCtx);
				it.postConstruct(result);
			}		
			return result;
			
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			logger.error("Exception while trying to instanciate class: ".concat(beanClass.getName()), e);
			throw new RuntimeException(e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void initialize(T beanInstance) throws RuntimeException {
		try {
			
			AnnotatedType<T> type = beanManager.createAnnotatedType((Class<T>)beanInstance.getClass());
			InjectionTarget<T> it = beanManager.createInjectionTarget(type);
			CreationalContext<T> cCtx = (CreationalContext<T>) beanManager.createCreationalContext(null);
			
			/* only inject dependencies */
			it.inject(beanInstance, cCtx);
			// it.postConstruct(beanInstance);
			
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			Throwable cause = Throwables.getRootCause(e);
			logger.error("Exception occured while trying to initialize bean: ".concat(cause.getMessage()), cause);
			throw new RuntimeException(e);
		}		
	}

	@Override
	public synchronized void terminate() {
		
		if (null != beanManager) {
			beanManager = null;			
			if (null != container)
				container.stopApplication(null);
			container = null;			
		}
		
	}	
	
	
		
}
