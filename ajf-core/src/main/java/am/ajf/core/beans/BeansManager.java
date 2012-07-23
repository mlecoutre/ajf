package am.ajf.core.beans;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import am.ajf.core.beans.api.BeanDefinitionsLoader;
import am.ajf.core.beans.api.BeanFactory;

import com.google.common.base.Strings;

public class BeansManager {

	private static final Map<Class<?>, Map<String, Set<BeanDefinition>>> beanDefinitionsMap = new ConcurrentHashMap<Class<?>, Map<String, Set<BeanDefinition>>>();

	public static final String BEAN_PROFILES_KEY = "am.ajf.beans.profiles";
	public static final String DEFAULT_BEAN_PROFILE = "__default";
	
	private static final BeanDefinitionsLoader defaultBeanDefinitionsLoader = new IniBeanDefinitionsLoaderImpl();

	private static Set<String> activeProfiles = null;
	 
	private BeansManager() {
		super();
	}

	public static Set<String> getActiveProfiles() {
		if (null != activeProfiles) {
			return activeProfiles;
		}
		processActiveProfiles();
		return activeProfiles;
		
	}

	private static synchronized void processActiveProfiles() {
		
		if (null != activeProfiles) {
			return;
		}
		
		activeProfiles = new LinkedHashSet<String>();
		String profiles = System.getProperty(BEAN_PROFILES_KEY);
		if (!Strings.isNullOrEmpty(profiles)) {
			String[] profilesArray = profiles.split("[ ]*,[ ]*");
			if (profilesArray.length > 0) {
				for (String profile : profilesArray) {
					String newProfile = profile.trim();
					if (newProfile.length() > 0) {
						activeProfiles.add(newProfile);
					}
				}
			}
		}
		if (!activeProfiles.contains(DEFAULT_BEAN_PROFILE)) {
			activeProfiles.add(DEFAULT_BEAN_PROFILE);
		}
	}
	
	/**
	 * 
	 * @param beanClass
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Set<BeanDefinition>> getBeanDefinitions(
			Class<?> beanInterface) throws Exception {

		if (beanDefinitionsMap.containsKey(beanInterface)) {
			return beanDefinitionsMap.get(beanInterface);
		}
		
		return loadBeanDefinitions(beanInterface);
		
	}
	
	public static String getDefaultBeanProfileName(Class<?> beanInterface) throws Exception {
		
		Set<String> nammedSet = getActiveProfiles();
		
		Map<String, Set<BeanDefinition>> beanDefinitionsMap = getBeanDefinitions(beanInterface);
		
		for (String requiredBeanProfile : nammedSet) {
			if (beanDefinitionsMap.containsKey(requiredBeanProfile)) {
				return requiredBeanProfile;
			}
		}
		
		if (beanDefinitionsMap.size() > 1) {
			return null;
		}
		else {
			// the first impl
			return beanDefinitionsMap.keySet().iterator().next();
		} 
		
	}
	
	public static boolean isDefaultBeanProfile(Class<?> beanInterface, String profileName) throws Exception {
		
		String defaultProfile = getDefaultBeanProfileName(beanInterface);
		if (null == defaultProfile) {
			return false;
		}
		
		return (defaultProfile.equals(profileName));
		
	}

	/**
	 * 
	 * @param beanClass
	 * @return
	 * @throws Exception
	 */
	public static <T> T getBean(Class<T> beanInterface) throws Exception {
		return getBean(beanInterface, null);
	}

	public static Set<Class<?>> getDeclaredBeanImplementations(Class<?> componentType) throws Exception {
		return defaultBeanDefinitionsLoader.getBeanImplementations(componentType);
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Set<Class<?>> getBeans() throws Exception {
		return defaultBeanDefinitionsLoader.getBeans();
	}
	
	
	public static Set<Class<?>> getNotConfiguredBeanImplementations(Class<?> classInterface)
			throws Exception, ClassNotFoundException {
		
		Set<Class<?>> beanImplementations = BeansManager.getDeclaredBeanImplementations(classInterface);
		
		Set<Class<?>> missingConfiguredBeans = new LinkedHashSet<Class<?>>();
		for (Class<?> beanImplementation : beanImplementations) {
			missingConfiguredBeans.add(beanImplementation);
		}
									
		Map<String, Set<BeanDefinition>> declarations = getBeanDefinitions(classInterface);
		Set<Entry<String, Set<BeanDefinition>>> entries = declarations
				.entrySet();
		for (Iterator<Entry<String, Set<BeanDefinition>>> iterator = entries
				.iterator(); iterator.hasNext();) {
			Entry<String, Set<BeanDefinition>> entry = (Entry<String, Set<BeanDefinition>>) iterator
					.next();
			BeanDefinition firstBeanConfig = entry.getValue().iterator().next();
			Class<?> firstBeanconfigClass  = firstBeanConfig.getBeanClass();
			
			if (missingConfiguredBeans.contains(firstBeanconfigClass)) {
				missingConfiguredBeans.remove(firstBeanconfigClass);
			}
			
		}
		return missingConfiguredBeans;
		
	}
	
	
	/**
	 * 
	 * @param beanClass
	 * @param beanProfile
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> beanClass, String beanProfile)
			throws Exception {

		Set<String> nammedSet = new LinkedHashSet<String>();
		
		if (Strings.isNullOrEmpty(beanProfile)) {
			nammedSet =  getActiveProfiles();
		}
		else {
			nammedSet.add(beanProfile);
		}
		
		
		Map<String, Set<BeanDefinition>> beanDefinitionsMap = getBeanDefinitions(beanClass);
		Set<BeanDefinition> beanDefinition = null;
		
		for (String requiredBeanProfile : nammedSet) {
			if (beanDefinitionsMap.containsKey(requiredBeanProfile)) {
				beanDefinition = beanDefinitionsMap
						.get(requiredBeanProfile);
				break;
			}
		}
				
		if (null == beanDefinition) {
			if (beanDefinitionsMap.size() > 1) {
				return null;
			}
			else {
				// the first impl
				beanDefinition = beanDefinitionsMap.values().iterator().next();
			}
		} 
		
		BeanDefinition firstBeanDefinition = beanDefinition
				.iterator().next();

		T beanInstance = (T) getBean(firstBeanDefinition);
		return beanInstance;
	}
	
	/**
	 * 
	 * @param beanDefinition
	 * @return
	 * @throws ClassNotFoundException
	 * @throws Exception
	 */
	public static Object getBean(BeanDefinition beanDefinition)
			throws ClassNotFoundException, Exception {
		
		BeanFactory factory = beanDefinition.getBeanFactory();
		Object beanInstance = factory.create(beanDefinition);
		return beanInstance;
	}
	
	/**
	 * 
	 * @param beanClass
	 * @return
	 * @throws Exception
	 */
	private synchronized static Map<String, Set<BeanDefinition>> loadBeanDefinitions(
			Class<?> beanClass) throws Exception {
		
		if (beanDefinitionsMap.containsKey(beanClass)) {
			return beanDefinitionsMap.get(beanClass);
		}
		Map<String, Set<BeanDefinition>> beanDeclarations = defaultBeanDefinitionsLoader
				.loadBeanDefinitions(beanClass);
		beanDefinitionsMap.put(beanClass, beanDeclarations);
		return beanDeclarations;
	}

}
