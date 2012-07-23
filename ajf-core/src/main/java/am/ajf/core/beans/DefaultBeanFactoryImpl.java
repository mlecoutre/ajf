package am.ajf.core.beans;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.PropertyConverter;

import am.ajf.core.ApplicationContext;
import am.ajf.core.beans.api.BeanFactory;
import am.ajf.core.configuration.ConfigurationHelper;
import am.ajf.core.utils.BeanUtils;

public class DefaultBeanFactoryImpl implements BeanFactory {

	public DefaultBeanFactoryImpl() {
		super();
	}

	/**
	 * 
	 * @param data
	 * @return a new bean instance
	 * @throws ClassNotFoundException
	 */
	protected static Object createBeanInstance(BeanDefinition data)
			throws ClassNotFoundException {

		Class<?> beanClazz = data.getBeanClass();
		return BeanUtils.newInstance(beanClazz);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * am.ajf.core.beans.BeanFactory#create(BeanDefinition)
	 */
	@Override
	public Object create(BeanDefinition data) throws ClassNotFoundException,
			ConfigurationRuntimeException, NamingException {

		Object result = resolve(data);
		if (null == result) {
			result = createBeanInstance(data);
			result = initialize(result, data);
		}
		return result;

	}

	/*
	 * (non-Javadoc)
	 * @see am.ajf.core.beans.api.BeanFactory#resolve(BeanDefinition)
	 */
	@Override
	public Object resolve(BeanDefinition data)
			throws NamingException {
		
		Object result = null;
		if (data.isNamed()) {
			String jndiName = data.getBeanJNDI();
			Context context = new InitialContext();
			result = context.lookup(jndiName);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see am.ajf.core.beans.api.BeanFactory#initialize(java.lang.Object,
	 * am.ajf.core.beans.BeanDefinition)
	 */
	@Override
	public Object initialize(Object beanInstance, BeanDefinition data)
			throws ConfigurationRuntimeException {

		Configuration contextConfiguration = ConfigurationHelper
				.mergeConfigurations(ApplicationContext.getConfiguration(),
						data.getConfiguration());

		Configuration beanConfiguration = data.getConfiguration();
		if (beanConfiguration != null) {
			for (Iterator<String> iterator = beanConfiguration.getKeys(); iterator
					.hasNext();) {
				String key = iterator.next();
				Object value = beanConfiguration.getProperty(key);
				initProperty(beanInstance, contextConfiguration, key, value);
			}
		}

		return beanInstance;
	}

	protected static void initProperty(Object bean,
			Configuration contextConfiguration, String propName, Object value)
			throws ConfigurationRuntimeException {
		if (!PropertyUtils.isWriteable(bean, propName)) {
			throw new ConfigurationRuntimeException("Property " + propName
					+ " cannot be set on " + bean.getClass().getName());
		}

		try {
			Object newValue = interpolate(value, contextConfiguration);
			org.apache.commons.beanutils.BeanUtils.setProperty(bean, propName,
					newValue);
		} catch (IllegalAccessException iaex) {
			throw new ConfigurationRuntimeException(iaex);
		} catch (InvocationTargetException itex) {
			throw new ConfigurationRuntimeException(itex);
		}
	}

	@SuppressWarnings("unchecked")
	protected static Object interpolate(Object objectValue,
			Configuration contextConfiguration) {

		if (null == objectValue)
			return null;

		if (objectValue instanceof List<?>) {
			List<Object> lov = (List<Object>) objectValue;

			if (lov.isEmpty())
				return null;

			List<Object> interpolatedLov = new ArrayList<Object>();
			for (Object value : lov) {
				Object interpolatedObject = PropertyConverter.interpolate(
						value, (AbstractConfiguration) contextConfiguration);
				interpolatedLov.add(interpolatedObject);
			}

			return interpolatedLov;

		} else {

			Object interpolatedObject = PropertyConverter.interpolate(
					objectValue, (AbstractConfiguration) contextConfiguration);
			return interpolatedObject;

		}

	}

}
