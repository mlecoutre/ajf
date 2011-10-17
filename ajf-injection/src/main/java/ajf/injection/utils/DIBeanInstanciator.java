package ajf.injection.utils;

import ajf.injection.InjectionContext;
import ajf.utils.BeanInstanciator;

public class DIBeanInstanciator implements BeanInstanciator {

	public DIBeanInstanciator() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ajf.utils.BeanInstanciator#instanciate(java.lang.Class)
	 */
	public Object instanciate(Class<?> beanClass) {
		Object bean = InjectionContext.getInstance().getInjector().getInstance(beanClass);
		return bean;
	}

}
