package ajf.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;

import ajf.cache.Cache;
import ajf.cache.impl.InfinispanEmbeddedCacheManagerImpl;
import ajf.logger.LoggerFactory;

public class ServiceLocator {

	private final static Logger logger = LoggerFactory.getLogger();

	private final static List<ServiceFactory> factories = new ArrayList<ServiceFactory>();
	private final static Cache<Class<?>, ServiceFactory> factoriesCache = new InfinispanEmbeddedCacheManagerImpl()
			.getCache(ServiceLocator.class.getName());

	static {
		initialize();
	}
	
	private ServiceLocator() {
		super();
	}

	private static void initialize() {
		try {
			ServiceLoader<ServiceFactory> loader = ServiceLoader
					.load(ServiceFactory.class);
			for (Iterator<ServiceFactory> iterator = loader.iterator(); iterator
					.hasNext();) {
				ServiceFactory service = iterator.next();
				registerServiceFactory(service);
			}
		}
		catch (Exception e) {
			logger.warn("Unable to get resources 'META-INF/services/"
					+ ServiceFactory.class.getName() + "'.", e);
		}

	}

	/**
	 * 
	 * @param serviceLocator
	 * @return
	 */
	public static ServiceFactory registerServiceFactory(
			ServiceFactory serviceFactory) {

		synchronized (factories) {

			if (!factories.contains(serviceFactory)) {
				factories.add(serviceFactory);

				Collections.sort(factories, new Comparator<ServiceFactory>() {

					/*
					 * @return a negative integer, zero, or a positive integer
					 * as the first argument is less than, equal to, or greater
					 * than the second.
					 */
					@Override
					public int compare(ServiceFactory o1, ServiceFactory o2) {
						return ((Integer) o1.getPriorityLevel()).compareTo(o2
								.getPriorityLevel());
					}

				});

			}
		}
		return serviceFactory;
	}

	/**
	 * 
	 * @param serviceLocator
	 * @return
	 */
	public static ServiceFactory unregisterServiceFactory(
			ServiceFactory serviceFactory) {
		
		synchronized (factories) {
			if (factories.contains(serviceFactory))
				factories.remove(serviceFactory);
		}
		return serviceFactory;
	}

	/**
	 * 
	 * @return
	 */
	public static Iterator<ServiceFactory> getServiceFactories() {
		return factories.iterator();
	}

	@SuppressWarnings("unchecked")
	public static <T> T getService(Class<?> serviceClass) throws ServiceLocatorException {

		if (!factories.isEmpty()) {

			Object serviceImpl = null;

			try {
				
				// if already served
				if (factoriesCache.contains(serviceClass)) {
					ServiceFactory factory = factoriesCache
							.get(serviceClass);
					serviceImpl = factory.get(serviceClass);
					return (T) serviceImpl;
				}

				// iterate on the differents factories
				for (ServiceFactory factory : factories) {
					if (factory.accept(serviceClass)) {
						factoriesCache.put(serviceClass, factory);
						serviceImpl = factory.get(serviceClass);
						return (T) serviceImpl;
					}
				}
			}
			catch (Exception e) {
				logger.error("Receive exception while trying to obtain a reference for the service '" + serviceClass.getName() + "'.", e);
			}
		}

		throw new ServiceLocatorException("Unable to obtain a reference for '"
				+ serviceClass.getName() + "'.");

	}

}
