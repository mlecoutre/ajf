package am.ajf.core.services;

import am.ajf.core.utils.BeanUtils;
import am.ajf.core.utils.ClassUtils;

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
	public <T> T get(Class<T> serviceClass) throws Exception {

		String serviceImplClassName = ClassUtils
				.processServiceImplName(serviceClass);

		Class<?> serviceImplClass = ClassUtils.loadClass(serviceImplClassName);
		T result = (T) BeanUtils.newInstance(serviceImplClass);
		return result;

	}

}
