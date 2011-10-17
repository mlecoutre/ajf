package ajf.services;

public interface ServiceFactory {

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
	<T> T lookup(Class<?> serviceClass) throws Exception;
	
	/**
	 * give the ServiceLocator priority level, default = 0
	 * 
	 * @return
	 */
	int getPriorityLevel();

}