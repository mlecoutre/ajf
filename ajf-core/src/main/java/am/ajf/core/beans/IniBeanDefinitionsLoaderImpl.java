package am.ajf.core.beans;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.slf4j.Logger;

import am.ajf.core.beans.api.BeanDefinitionBuilder;
import am.ajf.core.beans.api.BeanDefinitionsLoader;
import am.ajf.core.logger.LoggerFactory;
import am.ajf.core.utils.ClassUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class IniBeanDefinitionsLoaderImpl implements BeanDefinitionsLoader {

	private final static Logger logger = LoggerFactory
			.getLogger(IniBeanDefinitionsLoaderImpl.class);

	private final static ClassLoader classLoader = IniBeanDefinitionsLoaderImpl.class
			.getClassLoader();

	private final static Set<Class<?>> beansSet = new HashSet<Class<?>>();
	private final static Map<Class<?>, Set<Class<?>>> beanImplementationsMap = new ConcurrentHashMap<Class<?>, Set<Class<?>>>();

	private final static BeanDefinitionBuilder beanDefinitionBuilder = new IniBeanDefinitionBuilder();
	
	public IniBeanDefinitionsLoaderImpl() {
		super();
	}

	/* (non-Javadoc)
	 * @see am.ajf.core.beans.BeanDefinitionsLoader#loadBeanDefinitions(java.lang.Class)
	 */
	@Override
	public Map<String, Set<BeanDefinition>> loadBeanDefinitions(
			Class<?> componentType) throws IOException {

		// the result Map
		Map<String, Set<BeanDefinition>> resultBeansMap = new HashMap<String, Set<BeanDefinition>>();

		// try to load interface level defined beans
		loadBeanDefinitions(componentType, resultBeansMap);

		// list the available Bean implementations
		try {
			Set<Class<?>> implemsSet = getBeanImplementations(componentType);
			if (null != implemsSet) {
				for (Class<?> beanImplemClass : implemsSet) {
					loadBeanDefinitions(beanImplemClass, resultBeansMap);
				}
			}
		} catch (Exception e) {
			String message = String
					.format("Exception occured while listing the implementeations for '%s'.",
							componentType.getName());
			logger.error(message, e);
		}

		// sort the ExtendedBeanDeclarations
		sortBeansDefinitionsMapByOrdinal(resultBeansMap);

		return resultBeansMap;

	}

	private static void sortBeansDefinitionsMapByOrdinal(
			Map<String, Set<BeanDefinition>> resultBeansMap) {
		if (!resultBeansMap.isEmpty()) {
			Set<Entry<String, Set<BeanDefinition>>> entries = resultBeansMap
					.entrySet();

			for (Iterator<Entry<String, Set<BeanDefinition>>> iterator = entries
					.iterator(); iterator.hasNext();) {
				Entry<String, Set<BeanDefinition>> entry = (Entry<String, Set<BeanDefinition>>) iterator
						.next();
				// String key = entry.getKey();
				Set<BeanDefinition> beanDeclarations = entry
						.getValue();

				List<BeanDefinition> list = Lists
						.newArrayList(beanDeclarations.iterator());
				Collections.sort(list,
						new Comparator<BeanDefinition>() {

							@Override
							public int compare(BeanDefinition o1,
									BeanDefinition o2) {
								int compareTo = ((Integer) o1.getBeanOrdinal())
										.compareTo((Integer) o2
												.getBeanOrdinal());
								if (compareTo < 0)
									return 1;
								if (compareTo > 0)
									return -1;
								return 0;
							}

						});

				entry.setValue(new LinkedHashSet<BeanDefinition>(list));

			}
		}
	}

	private static void loadBeanDefinitions(Class<?> componentType,
			Map<String, Set<BeanDefinition>> resultBeansMap)
			throws IOException {

		String iniFileName = String.format("META-INF/profiles/%s.ini",
				componentType.getName());
		Enumeration<URL> configURLs = classLoader.getResources(iniFileName);

		/*
		 * Map<String, Set<ExtendedBeanDeclaration>> beansMap =
		 * loadBeanDeclarations(configURLs, componentType, resultBeansMap);
		 */
		loadBeanDefinitions(configURLs, componentType, resultBeansMap);
		// copyBeans(beansMap, resultBeansMap);

		// return beansMap;
	}

	private static void loadBeanDefinitions(Enumeration<URL> configURLs,
			Class<?> beanImplemClass,
			Map<String, Set<BeanDefinition>> resultBeansMap) {

		// Map<String, Set<ExtendedBeanDeclaration>> beansMap = new
		// HashMap<String, Set<ExtendedBeanDeclaration>>();

		if (null != configURLs) {
			while (configURLs.hasMoreElements()) {

				URL configUrl = configURLs.nextElement();
				try {

					// load the configured beans map
					/*
					 * Map<String, Set<ExtendedBeanDeclaration>> localBeansMap =
					 * loadBeanDeclarations(configUrl, beanImplemClass,
					 * resultBeansMap);
					 */
					loadBeanDefinitions(configUrl, beanImplemClass,
							resultBeansMap);

					// copy localBeansMap in beansMap
					// copyBeans(localBeansMap, beansMap);

				} catch (ConfigurationException e) {
					String message = String.format(
							"Exception occured while loading the file '%s'.",
							configUrl.toExternalForm());
					logger.error(message, e);
				}

			}
		}

		// return beansMap;

	}

	private static void loadBeanDefinitions(URL configUrl,
			Class<?> defaultComponentImplClass,
			Map<String, Set<BeanDefinition>> resultBeansMap)
			throws ConfigurationException {

		logger.info(configUrl.toExternalForm());
		HierarchicalINIConfiguration iniConfig = new HierarchicalINIConfiguration();
		iniConfig.setDelimiterParsingDisabled(true);
		iniConfig.load(configUrl);
		
		Set<String> sections = iniConfig.getSections();

		for (String sectionName : sections) {

			try {
				Configuration sectionConfig = iniConfig.getSection(sectionName);
				
				BeanDefinition beanDeclaration = beanDefinitionBuilder.build(defaultComponentImplClass, sectionName, sectionConfig);
				registerBeanDefinition(resultBeansMap, sectionName,
						beanDeclaration);

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

		}
	}

	@SuppressWarnings("unused")
	private static void copyBeans(
			Map<String, Set<BeanDefinition>> srcBeansMap,
			Map<String, Set<BeanDefinition>> targetBeansMap) {

		if (!srcBeansMap.isEmpty()) {
			Set<Entry<String, Set<BeanDefinition>>> localBeansMapEntries = srcBeansMap
					.entrySet();
			for (Iterator<Entry<String, Set<BeanDefinition>>> iterator = localBeansMapEntries
					.iterator(); iterator.hasNext();) {
				Entry<String, Set<BeanDefinition>> entry = (Entry<String, Set<BeanDefinition>>) iterator
						.next();
				Set<BeanDefinition> beanDeclarations = entry
						.getValue();
				for (BeanDefinition beanDeclaration : beanDeclarations) {
					registerBeanDefinition(targetBeansMap, entry.getKey(),
							beanDeclaration);
				}
			}
		}
	}

	private static void registerBeanDefinition(
			Map<String, Set<BeanDefinition>> beansMap,
			String beanName, BeanDefinition beanDeclaration) {

		String newBeanName = beanName;
		if (Strings.isNullOrEmpty(newBeanName))
			newBeanName = BeansManager.DEFAULT_BEAN_PROFILE;

		Set<BeanDefinition> beanInstances = (Set<BeanDefinition>) beansMap
				.get(newBeanName);
		if (null == beanInstances) {
			beanInstances = new HashSet<BeanDefinition>();
			beansMap.put(newBeanName, beanInstances);
		}
		beanInstances.add(beanDeclaration);
	}

	/* (non-Javadoc)
	 * @see am.ajf.core.beans.BeanDefinitionsLoader#getBeanImplementations(java.lang.Class)
	 */
	@Override
	public Set<Class<?>> getBeanImplementations(Class<?> componentType)
			throws Exception {

		if (beanImplementationsMap.containsKey(componentType)) {
			return beanImplementationsMap.get(componentType);
		}

		Set<Class<?>> implemsSet = new HashSet<Class<?>>();

		String resourceName = String.format("META-INF/beans/%s",
				componentType.getName());
		Enumeration<URL> resources = classLoader.getResources(resourceName);

		if (null != resources) {
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				logger.info(url.toExternalForm());

				// load the implems list
				InputStream is = null;
				try {
					is = url.openStream();
					Properties props = new Properties();
					props.load(is);
					is.close();

					Set<Object> keys = props.keySet();
					for (Object key : keys) {
						String implemClassName = String.valueOf(key);
						logger.info(String.format("Find class '%s'.", key));

						try {
							Class<?> beanImplemClass = ClassUtils
									.getClass(implemClassName);
							implemsSet.add(beanImplemClass);
						} catch (Exception e) {
							String message = String
									.format("Exception occured while loading class '%s'.",
											implemClassName);
							logger.error(message, e);
						}

					}
				} catch (Exception e) {
					String message = String.format(
							"Exception occured while loading the file '%s'.",
							url.toExternalForm());
					logger.error(message, e);
				} finally {
					if (null != is) {
						try {
							is.close();
						} catch (Exception e) {
							String message = String
									.format("Exception occured while closing the file '%s'.",
											url.toExternalForm());
							logger.warn(message);
						}
					}
					is = null;
				}

			}

		}

		beanImplementationsMap.put(componentType, implemsSet);
		return implemsSet;

	}

	/* (non-Javadoc)
	 * @see am.ajf.core.beans.BeanDefinitionsLoader#getBeans()
	 */
	@Override
	public Set<Class<?>> getBeans()
			throws Exception {

		if (!beansSet.isEmpty()) {
			return beansSet;
		}

		Set<Class<?>> beans = new HashSet<Class<?>>();
		
		String resourceName = String.format("META-INF/beans.ini");
		Enumeration<URL> resources = classLoader.getResources(resourceName);

		if (null != resources) {
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				logger.info(url.toExternalForm());

				// load the beans list
				InputStream is = null;
				try {
					is = url.openStream();
					Properties props = new Properties();
					props.load(is);
					is.close();

					Set<Object> keys = props.keySet();
					for (Object key : keys) {
						String implemClassName = String.valueOf(key);
						logger.info(String.format("Find class '%s'.", key));

						try {
							Class<?> beanImplemClass = ClassUtils
									.getClass(implemClassName);
							beans.add(beanImplemClass);
						} catch (Exception e) {
							String message = String
									.format("Exception occured while loading bean '%s'.",
											implemClassName);
							logger.error(message, e);
						}

					}
				} catch (Exception e) {
					String message = String.format(
							"Exception occured while loading the file '%s'.",
							url.toExternalForm());
					logger.error(message, e);
				} finally {
					if (null != is) {
						try {
							is.close();
						} catch (Exception e) {
							String message = String
									.format("Exception occured while closing the file '%s'.",
											url.toExternalForm());
							logger.warn(message);
						}
					}
					is = null;
				}

			}

		}

		for (Class<?> bean : beans) {
			beansSet.add(bean);
		}
		
		return beansSet;

	}
	
}
