package ajf.injection.utils;

import org.slf4j.Logger;

import ajf.injection.InjectionContext;
import ajf.logger.LoggerFactory;
import ajf.utils.BeanUtilsDelegate;

import com.google.inject.Injector;

public class DIBeanUtilsDelegate 
	implements BeanUtilsDelegate {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Injector injector = InjectionContext.getInstance().getInjector();
	
	/**
	 * Default constructor 
	 */
	public DIBeanUtilsDelegate() {
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ajf.utils.BeanUtilsDelegate#initialize(java.lang.Object)
	 */
	public void initialize(Object beanInstance) throws RuntimeException {
		try {
			injector.injectMembers(beanInstance);
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			logger.error("Exception while trying to initialize bean: ".concat(beanInstance.getClass().getName()), e);
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ajf.utils.BeanUtilsDelegate#instanciate(java.lang.Class)
	 */
	public Object instanciate(Class<?> beanClass) throws RuntimeException {
		try {
			Object bean = injector.getInstance(beanClass);
			return bean;
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			logger.error("Exception while trying to instanciate class: ".concat(beanClass.getName()), e);
			throw new RuntimeException(e);
		}
	}
		
}
