package am.ajf.core.utils;

public interface BeanFactory {
	
	/**
	 * 
	 * @param beanClass
	 * @return a new bean instance
	 * @throws Exception
	 */
	<T> T instanciate(Class<T> beanClass) throws RuntimeException;
	
	/**
	 * initialize the given bean instance
	 * @param instance
	 * @throws Exception
	 */
	<T> void initialize(T beanInstance) throws RuntimeException;

	/**
	 * terminate the delegate
	 */
	void terminate();
	
}
