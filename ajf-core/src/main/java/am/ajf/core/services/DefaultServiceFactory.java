package am.ajf.core.services;

import am.ajf.core.utils.ClassUtils;

public class DefaultServiceFactory implements ServiceFactory {

	// default service provider strategy
	private ServiceProviderStrategy defaultStrategy = new DefaultServiceProviderStrategy();

	public DefaultServiceFactory() {
		super();
	}

	public <T> T get(Class<T> serviceClass) throws Exception {

		T serviceImpl = defaultStrategy.get(serviceClass);
		return (T) serviceImpl;

	}

	public boolean accept(Class<?> serviceClass) {
		return ClassUtils.isService(serviceClass);
	}

	public int getOrdinal() {
		return 0;
	}

}
