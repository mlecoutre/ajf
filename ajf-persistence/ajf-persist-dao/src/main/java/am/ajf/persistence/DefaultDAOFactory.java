package am.ajf.persistence;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import am.ajf.core.services.ServiceFactory;
import am.ajf.core.utils.ClassUtils;

public class DefaultDAOFactory implements ServiceFactory {
	
	public DefaultDAOFactory() {
		super();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> daoClass) throws Exception {

		// get the invocationHandler for DAO class
		InvocationHandler invocationHandler = new JpaDAOProxy(daoClass);
				
		// instanciate the proxy
		Object proxy = Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { daoClass }, invocationHandler);
						
		// return the proxy
		return (T) proxy;

	}

	@Override
	public boolean accept(Class<?> serviceClass) {
 		return ClassUtils.isDAO(serviceClass);
	}

	@Override
	public int getOrdinal() {
		return 0;
	}

}
