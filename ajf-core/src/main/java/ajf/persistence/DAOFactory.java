package ajf.persistence;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import ajf.services.MockContext;

public abstract class DAOFactory {

	/**
	 * 
	 * @param daoClass
	 * @param inJTA
	 * @return 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T extends DAO> T getDAO(Class<?> daoClass) throws Exception {

		// is the DAO mocked
		Object mock = MockContext.lookup(daoClass); 
		if ((null != mock) && (mock instanceof DAO)) {
			return (T) mock;
		}
		
		// get the invocationHandler for DAO class
		InvocationHandler invocationHandler = new JpaDAOProxy(daoClass);
				
		// instanciate the proxy
		Object proxy = Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { daoClass }, invocationHandler);
						
		// return the proxy
		return (T) proxy;

	}

}
