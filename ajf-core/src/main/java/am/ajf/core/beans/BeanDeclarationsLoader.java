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

import am.ajf.core.ApplicationContext;
import am.ajf.core.logger.LoggerFactory;
import am.ajf.core.utils.ClassUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class BeanDeclarationsLoader {

	private final static Logger logger = LoggerFactory
			.getLogger(BeanDeclarationsLoader.class);

	private final static ClassLoader classLoader = BeanDeclarationsLoader.class
			.getClassLoader();

	private final static Set<Class<?>> beansSet = new HashSet<Class<?>>();
	private final static Map<Class<?>, Set<Class<?>>> beanImplementationsMap = new ConcurrentHashMap<Class<?>, Set<Class<?>>>();

	private BeanDeclarationsLoader() {
		super();
	}

	public static Map<String, Set<ExtendedBeanDeclaration>> loadBeanDeclarations(
			Class<?> componentType) throws IOException {

		// the result Map
		Map<String, Set<ExtendedBeanDeclaration>> resultBeansMap = new HashMap<String, Set<ExtendedBeanDeclaration>>();

		// try to load interface level defined beans
		loadBeanDeclarations(componentType, resultBeansMap);

		// list the available Bean implementations
		try {
			Set<Class<?>> implemsSet = getBeanImplementations(componentType);
			if (null != implemsSet) {
				for (Class<?> beanImplemClass : implemsSet) {
					loadBeanDeclarations(beanImplemClass, resultBeansMap);
				}
			}
		} catch (Exception e) {
			String message = String
					.format("Exception occured while listing the implementeations for '%s'.",
							componentType.getName());
			logger.error(message, e);
		}

		// sort the ExtendedBeanDeclarations
		if (!resultBeansMap.isEmpty()) {
			Set<Entry<String, Set<ExtendedBeanDeclaration>>> entries = resultBeansMap
					.entrySet();

			for (Iterator<Entry<String, Set<ExtendedBeanDeclaration>>> iterator = entries
					.iterator(); iterator.hasNext();) {
				Entry<String, Set<ExtendedBeanDeclaration>> entry = (Entry<String, Set<ExtendedBeanDeclaration>>) iterator
						.next();
				// String key = entry.getKey();
				Set<ExtendedBeanDeclaration> beanDeclarations = entry
						.getValue();

				List<ExtendedBeanDeclaration> list = Lists
						.newArrayList(beanDeclarations.iterator());
				Collections.sort(list,
						new Comparator<ExtendedBeanDeclaration>() {

							@Override
							public int compare(ExtendedBeanDeclaration o1,
									ExtendedBeanDeclaration o2) {
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

				entry.setValue(new LinkedHashSet<ExtendedBeanDeclaration>(list));

			}
		}

		return resultBeansMap;

	}

	private static void loadBeanDeclarations(Class<?> componentType,
			Map<String, Set<ExtendedBeanDeclaration>> resultBeansMap)
			throws IOException {

		String iniFileName = String.format("META-INF/config/%s.ini",
				componentType.getName());
		Enumeration<URL> configURLs = classLoader.getResources(iniFileName);

		/*
		 * Map<String, Set<ExtendedBeanDeclaration>> beansMap =
		 * loadBeanDeclarations(configURLs, componentType, resultBeansMap);
		 */
		loadBeanDeclarations(configURLs, componentType, resultBeansMap);
		// copyBeans(beansMap, resultBeansMap);

		// return beansMap;
	}

	private static void loadBeanDeclarations(Enumeration<URL> configURLs,
			Class<?> beanImplemClass,
			Map<String, Set<ExtendedBeanDeclaration>> resultBeansMap) {

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
					loadBeanDeclarations(configUrl, beanImplemClass,
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

	private static void loadBeanDeclarations(URL configUrl,
			Class<?> defaultComponentImplClass,
			Map<String, Set<ExtendedBeanDeclaration>> resultBeansMap)
			throws ConfigurationException {

		logger.info(configUrl.toExternalForm());
		HierarchicalINIConfiguration iniConfig = new HierarchicalINIConfiguration(
				configUrl);
		Set<String> sections = iniConfig.getSections();

		// Map<String, Set<ExtendedBeanDeclaration>> localBeansMap = new
		// HashMap<String, Set<ExtendedBeanDeclaration>>();

		for (String sectionName : sections) {

			try {
				Configuration sectionConfig = iniConfig.getSection(sectionName);
				ExtendedBeanDeclaration beanDeclaration = new IniBeanDeclarationImpl(
						defaultComponentImplClass,
						ApplicationContext.getConfiguration(), sectionConfig,
						sectionName);
				// registerBeanDeclaration(localBeansMap, sectionName,
				// beanDeclaration);
				registerBeanDeclaration(resultBeansMap, sectionName,
						beanDeclaration);

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

		}

		// return localBeansMap;
		// return resultBeansMap;
	}

	@SuppressWarnings("unused")
	private static void copyBeans(
			Map<String, Set<ExtendedBeanDeclaration>> srcBeansMap,
			Map<String, Set<ExtendedBeanDeclaration>> targetBeansMap) {

		if (!srcBeansMap.isEmpty()) {
			Set<Entry<String, Set<ExtendedBeanDeclaration>>> localBeansMapEntries = srcBeansMap
					.entrySet();
			for (Iterator<Entry<String, Set<ExtendedBeanDeclaration>>> iterator = localBeansMapEntries
					.iterator(); iterator.hasNext();) {
				Entry<String, Set<ExtendedBeanDeclaration>> entry = (Entry<String, Set<ExtendedBeanDeclaration>>) iterator
						.next();
				Set<ExtendedBeanDeclaration> beanDeclarations = entry
						.getValue();
				for (ExtendedBeanDeclaration beanDeclaration : beanDeclarations) {
					registerBeanDeclaration(targetBeansMap, entry.getKey(),
							beanDeclaration);
				}
			}
		}
	}

	private static void registerBeanDeclaration(
			Map<String, Set<ExtendedBeanDeclaration>> beansMap,
			String beanName, ExtendedBeanDeclaration beanDeclaration) {

		String newBeanName = beanName;
		if (Strings.isNullOrEmpty(newBeanName))
			newBeanName = BeansManager.DEFAULT_BEAN_NAME;

		Set<ExtendedBeanDeclaration> beanInstances = (Set<ExtendedBeanDeclaration>) beansMap
				.get(newBeanName);
		if (null == beanInstances) {
			beanInstances = new HashSet<ExtendedBeanDeclaration>();
			beansMap.put(newBeanName, beanInstances);
		}
		beanInstances.add(beanDeclaration);
	}

	/**
	 * 
	 * @param componentType
	 * @return the set of implementations classes
	 * @throws Exception
	 */
	public static Set<Class<?>> getBeanImplementations(Class<?> componentType)
			throws Exception {

		if (beanImplementationsMap.containsKey(componentType)) {
			return beanImplementationsMap.get(componentType);
		}

		Set<Class<?>> implemsSet = new HashSet<Class<?>>();

		String resourceName = String.format("META-INF/services/%s",
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

	public static Set<Class<?>> getBeans()
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
