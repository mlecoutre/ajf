package am.ajf.core.beans;

import org.apache.commons.configuration.beanutils.BeanDeclaration;

public interface ExtendedBeanDeclaration extends BeanDeclaration {

	/**
	 * 
	 * @return the Bean loading order
	 */
	int getBeanOrdinal();

	/**
	 * 
	 * @return the default Bean Class
	 */
	Class<?> getDefaultBeanClass();

}