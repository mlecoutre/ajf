package am.ajf.core.beans.api;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import am.ajf.core.beans.BeanDefinition;

public interface BeanDefinitionsLoader {

	Map<String, Set<BeanDefinition>> loadBeanDefinitions(
			Class<?> componentType) throws IOException;

	Set<Class<?>> getBeanImplementations(Class<?> componentType)
			throws Exception;

	Set<Class<?>> getBeans() throws Exception;

}