package am.ajf.core.beans;

import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.core.beans.api.BeanDefinitionBuilder;
import am.ajf.core.utils.ClassUtils;

import com.google.common.base.Strings;

public class IniBeanDefinitionBuilder implements BeanDefinitionBuilder {

	private static final int BEAN_ORDINAL_DEFAULT_VALUE = 1;

	/** Constant for the prefix of reserved attributes. */
	public static final String RESERVED_PREFIX = "config-";

	/** Constant for the bean class attribute. */
	public static final String BEAN_ORDINAL = RESERVED_PREFIX + "ordinal";
	
	public static final String BEAN_JNDI = RESERVED_PREFIX + "jndi";

	/** Constant for the bean class attribute. */
	public static final String BEAN_CLASS = RESERVED_PREFIX + "class";

	/** Constant for the bean factory attribute. */
	public static final String BEAN_FACTORY = RESERVED_PREFIX + "factory";

	private static final Logger logger = LoggerFactory
			.getLogger(IniBeanDefinitionBuilder.class);

	public IniBeanDefinitionBuilder() {
		super();
	}

	/* (non-Javadoc)
	 * @see am.ajf.core.beans.BeanDefinitionBuilder#build(java.lang.Class, org.apache.commons.configuration.Configuration, java.lang.String)
	 */
	@Override
	public BeanDefinition build(Class<?> defaultBeanClass,
			String beanProfile,
			Configuration iniConfiguration)
			throws ClassNotFoundException {

		String beanFactoryClassName = iniConfiguration.getString(BEAN_FACTORY);
		String beanClassName = iniConfiguration.getString(BEAN_CLASS);
		
		String beanJNDI = iniConfiguration.getString(BEAN_JNDI);
		
		int beanOrdinal = 1;
		try {
			beanOrdinal = iniConfiguration.getInt(BEAN_ORDINAL,
					BEAN_ORDINAL_DEFAULT_VALUE);
		} catch (Exception e) {
			beanOrdinal = 1;
		}
		
		Class<?> beanClass = getBeanClass(defaultBeanClass, beanClassName);
		BeanDefinition beanDefinition = new BeanDefinition(beanProfile,
				beanClass, beanJNDI, beanOrdinal,
				getBeanFactoryClass(beanFactoryClassName), 
				getProperties(iniConfiguration));

		return beanDefinition;

	}

	private static Class<?> getBeanClass(Class<?> defaultBeanClass,
			String beanClassName) throws ClassNotFoundException {

		Class<?> beanClazz = null;
		if (!Strings.isNullOrEmpty(beanClassName)) {
			beanClazz = ClassUtils.getClass(beanClassName);
		}
		if (null == beanClazz) {
			beanClazz = defaultBeanClass;
		}
		return beanClazz;
	}

	private static Class<?> getBeanFactoryClass(String beanFactoryClassName)
			throws ClassNotFoundException {

		Class<?> beanClazz = null;
		if (!Strings.isNullOrEmpty(beanFactoryClassName)) {
			try {
				beanClazz = ClassUtils.getClass(beanFactoryClassName);
			} catch (ClassNotFoundException e) {
				logger.error(String.format("The Class '%s' can not be found.",
						beanFactoryClassName), e);
				return null;
			}
		}

		return beanClazz;
	}

	private static Configuration getProperties(
			Configuration iniConfiguration) {

		PropertiesConfiguration props = new PropertiesConfiguration();
		props.setDelimiterParsingDisabled(true);
		
		for (Iterator<String> iterator = iniConfiguration.getKeys(); iterator
				.hasNext();) {
			String key = (String) iterator.next();
			if (!isReservedKey(key)) {
				Object lov = iniConfiguration.getList(key);
				props.addProperty(key, lov);
			}
		}

		return props;

	}

	private static boolean isReservedKey(String key) {
		return key.startsWith(RESERVED_PREFIX);
	}

}
