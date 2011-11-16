package ajf.services;

import ajf.utils.BeanUtils;
import ajf.utils.ClassUtils;

public class DefaultServiceProviderStrategy implements ServiceProviderStrategy {
	
	public DefaultServiceProviderStrategy() {
		super();
	}

	@Override
	public boolean accept(Class<?> serviceClass) {
		// this is the default strategy
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Class<?> serviceClass) throws Exception {

		String serviceImplClassName = ClassUtils
				.processServiceClassName(serviceClass);

		Class<?> serviceImplClass = ClassUtils.loadClass(serviceImplClassName);
		Object serviceImpl = BeanUtils.newInstance(
				serviceImplClass);
		return (T) serviceImpl;

	}

}
