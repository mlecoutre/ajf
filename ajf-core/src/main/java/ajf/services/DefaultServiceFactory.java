package ajf.services;

import ajf.utils.ClassUtils;

public class DefaultServiceFactory implements ServiceFactory {

	// service provider for singleton strategy
	private ServiceProviderStrategy singletonStrategy = new SingletonServiceProviderStrategy();
	// default service provider strategy
	private ServiceProviderStrategy defaultStrategy = new DefaultServiceProviderStrategy();

	public DefaultServiceFactory() {
		super();
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<?> serviceClass) throws Exception {

		Object serviceImpl = null;
		
		if (singletonStrategy.accept(serviceClass)) {
			serviceImpl = singletonStrategy.get(serviceClass);
		}
		
		if (null == serviceImpl) {
			serviceImpl = defaultStrategy.get(serviceClass);
		}
		
		return (T) serviceImpl;

	}

	public boolean accept(Class<?> serviceClass) {
		return ClassUtils.isService(serviceClass);
	}

	public int getPriorityLevel() {
		return 0;
	}

}
