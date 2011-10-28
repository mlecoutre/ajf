package ajf.persistence;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import ajf.services.ServiceFactory;
import ajf.utils.ClassUtils;

public class DefaultDAOFactory implements ServiceFactory {
	
	public DefaultDAOFactory() {
		super();
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<?> daoClass) throws Exception {

		// get the invocationHandler for DAO class
		InvocationHandler invocationHandler = new JpaDAOProxy(daoClass);
				
		// instanciate the proxy
		Object proxy = Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { daoClass }, invocationHandler);
						
		// return the proxy
		return (T) proxy;

	}

	public boolean accept(Class<?> serviceClass) {
 		return ClassUtils.isDAO(serviceClass);
	}

	public int getPriorityLevel() {
		return 0;
	}

}
