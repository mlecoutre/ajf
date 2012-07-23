package am.ajf.core.beans.api;

import org.apache.commons.configuration.Configuration;

import am.ajf.core.beans.BeanDefinition;

public interface BeanDefinitionBuilder {

	BeanDefinition build(Class<?> defaultBeanClass, 
			String beanProfile,
			Configuration configuration)
			throws ClassNotFoundException;

}