package ajf.utils;

public abstract class AbstractBeanInitializer 
	extends BeanUtils 
	implements BeanInitializer {

	/**
	 * @return a BeanInitializer instance
	 */
	public abstract BeanInitializer getInstance();

	/**
	 * register as BeanInitializer
	 */
	public void register() {
		registerBeanInitializer(getInstance());
	}

	/**
	 * unregister BeanInitializer
	 */
	public void unregister() {
		unregisterBeanInitializer(getInstance());
	}
	
	/**
	 * clear all the BeanInitializers
	 */
	public void clearBeanInitializers() {
		if (null != initializers) 
			initializers.clear();
	}


}
