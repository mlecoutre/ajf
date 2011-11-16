package ajf.services;

public interface ServiceProviderStrategy {

	/**
	 * is able to serve a specific interface
	 * 
	 * @param serviceClass
	 * @return
	 */
	boolean accept(Class<?> serviceClass);
	
	/**
	 * lookup a service implementation
	 * 
	 * @param <T>
	 * @param serviceClass
	 * @return
	 * @throws Exception
	 */
	<T> T get(Class<?> serviceClass) throws Exception;
	
}
