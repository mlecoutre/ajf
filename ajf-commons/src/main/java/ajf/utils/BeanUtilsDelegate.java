package ajf.utils;

public interface BeanUtilsDelegate {
	
	/**
	 * 
	 * @param beanClass
	 * @return a new bean instance
	 * @throws Exception
	 */
	Object instanciate(Class<?> beanClass) throws RuntimeException;
	
	/**
	 * initialize the given bean instance
	 * @param instance
	 * @throws Exception
	 */
	void initialize(Object beanInstance) throws RuntimeException;
	
}
