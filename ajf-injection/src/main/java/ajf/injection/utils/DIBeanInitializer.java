package ajf.injection.utils;

import ajf.injection.InjectionContext;
import ajf.utils.BeanInitializer;
import ajf.utils.BeanUtils;

public class DIBeanInitializer 
	implements BeanInitializer {
	
	/**
	 * Default constructor 
	 */
	public DIBeanInitializer() {
		super();
		
		BeanUtils.getInstance().setBeanInstanciator(new DIBeanInstanciator());
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see ajf.utils.BeanInitializer#initialize(java.lang.Object)
	 */
	public void initialize(Object instance) {
		InjectionContext.getInstance().getInjector().injectMembers(instance);
	}
		
}
