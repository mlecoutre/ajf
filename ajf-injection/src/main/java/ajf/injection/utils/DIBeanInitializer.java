package ajf.injection.utils;

import ajf.injection.DependenciesInjector;
import ajf.utils.BeanInitializer;

public class DIBeanInitializer 
	implements BeanInitializer {
	
	private final static BeanInitializer beanInstance = new DIBeanInitializer(); 

	/**
	 * Default constructor 
	 */
	public DIBeanInitializer() {
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ajf.utils.BeanInitializer#initialize(java.lang.Object)
	 */
	public void initialize(Object instance) {
		DependenciesInjector.inject(instance);
	}

	@Override
	public BeanInitializer getInstance() {
		return beanInstance;
	}
	
}
