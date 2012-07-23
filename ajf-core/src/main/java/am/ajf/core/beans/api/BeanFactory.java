package am.ajf.core.beans.api;

import am.ajf.core.beans.BeanDefinition;

public interface BeanFactory {

	/**
	 * 
	 * @param beanDefinition
	 * @return
	 * @throws Exception
	 */
	Object create(BeanDefinition data) throws Exception;
	
	/**
	 * 
	 * @param beanInstance
	 * @param beanDefinition
	 * @return
	 */
	Object initialize(Object beanInstance, BeanDefinition data);

	/**
	 * 
	 * @param beanDefinition
	 * @return
	 * @throws NamingException
	 */
	Object resolve(BeanDefinition data) throws Exception;

}