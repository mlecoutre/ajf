package ajf.injection.utils;

import ajf.injection.InjectionContext;
import ajf.utils.BeanUtilsDelegate;

import com.google.inject.Injector;

public class DIBeanUtilsDelegate 
	implements BeanUtilsDelegate {
	
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
	public void initialize(Object beanInstance) {
		injector.injectMembers(beanInstance);
	}

	/*
	 * (non-Javadoc)
	 * @see ajf.utils.BeanUtilsDelegate#instanciate(java.lang.Class)
	 */
	public Object instanciate(Class<?> beanClass) throws Exception {
		Object bean = injector.getInstance(beanClass);
		return bean;
	}
		
}
