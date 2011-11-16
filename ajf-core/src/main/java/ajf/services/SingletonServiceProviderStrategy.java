package ajf.services;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

public class SingletonServiceProviderStrategy extends
		DefaultServiceProviderStrategy {

	public Object singletonServicesToken = new Object();
	public Map<String, Object> singletonServicesMap = new HashMap<String, Object>();

	public SingletonServiceProviderStrategy() {
		super();
	}

	@Override
	public boolean accept(Class<?> serviceClass) {

		boolean res = serviceClass.isAnnotationPresent(Singleton.class);
		return res;

	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Class<?> serviceClass) throws Exception {

		Object serviceImpl = null;

		String serviceKey = serviceClass.getName();
		if (singletonServicesMap.containsKey(serviceKey)) {
			serviceImpl = singletonServicesMap.get(serviceKey);
		}

		synchronized (singletonServicesToken) {

			if (singletonServicesMap.containsKey(serviceKey)) {
				serviceImpl = singletonServicesMap.get(serviceKey);
			}
			else {
				serviceImpl = super.get(serviceClass);
				singletonServicesMap.put(serviceKey, serviceImpl);
			}

		}

		return (T) serviceImpl;

	}

}
