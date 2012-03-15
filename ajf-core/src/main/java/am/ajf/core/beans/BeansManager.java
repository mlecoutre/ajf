package am.ajf.core.beans;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import am.ajf.core.utils.BeanUtils;
import am.ajf.core.utils.ClassUtils;

import com.google.common.base.Strings;

public class BeansManager {

	private final static Map<Class<?>, Map<String, Set<ExtendedBeanDeclaration>>> beanDeclarationsMap = new ConcurrentHashMap<Class<?>, Map<String, Set<ExtendedBeanDeclaration>>>();

	public static final String DEFAULT_BEAN_NAME = "__default";

	private BeansManager() {
		super();
	}

	public static Map<String, Set<ExtendedBeanDeclaration>> getBeanDeclarations(
			Class<?> beanClass) throws Exception {

		if (beanDeclarationsMap.containsKey(beanClass)) {
			return beanDeclarationsMap.get(beanClass);
		}
		return loadBeanDeclarations(beanClass);

	}

	public static <T> T getDefaultBean(Class<T> beanClass) throws Exception {
		return getBean(beanClass, null);
	}

	public static Set<Class<?>> getBeanImplementations(Class<?> componentType) throws Exception {
		return BeanDeclarationsLoader.getBeanImplementations(componentType);
	}
	
	public static Set<Class<?>> getBeans() throws Exception {
		return BeanDeclarationsLoader.getBeans();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> beanClass, String beanRole)
			throws Exception {

		String nammed = beanRole;
		if (Strings.isNullOrEmpty(nammed)) {
			nammed =  BeansManager.DEFAULT_BEAN_NAME;
		}
		
		Map<String, Set<ExtendedBeanDeclaration>> beanDeclarationsMap = loadBeanDeclarations(beanClass);
		
		Set<ExtendedBeanDeclaration> beanDeclarations = null;
		if (!beanDeclarationsMap.containsKey(nammed)) {
			if (beanDeclarationsMap.size() > 1) {
				return null;
			}
			else {
				// the first impl
				beanDeclarations = beanDeclarationsMap.values().iterator().next();
			}
		} 
		else {
			// the named impl
			beanDeclarations = beanDeclarationsMap
				.get(nammed);
		}
		ExtendedBeanDeclaration extendedBeanDeclaration = beanDeclarations
				.iterator().next();

		T beanInstance = (T) getBean(extendedBeanDeclaration);
		return beanInstance;
	}

	public static Object getBean(ExtendedBeanDeclaration extendedBeanDeclaration)
			throws ClassNotFoundException, Exception {
		
		BeanFactory factory = getBeanFactory(extendedBeanDeclaration);
		 
		Object beanInstance = factory.createBean(extendedBeanDeclaration);
		return beanInstance;
	}

	public static BeanFactory getBeanFactory(
			ExtendedBeanDeclaration extendedBeanDeclaration)
			throws ClassNotFoundException {
		
		BeanFactory factory = BeanFactory.DEFAULT;
		String factoryName = extendedBeanDeclaration.getBeanFactoryName();
		if (!Strings.isNullOrEmpty(factoryName)) {
			Class<?> factoryClass = ClassUtils.getClass(factoryName);
			factory = (BeanFactory) BeanUtils.newInstance(factoryClass);
		}
		return factory;
	}
	
	public static <T> T initBean(T beanInstance, ExtendedBeanDeclaration extendedBeanDeclaration)
			throws ClassNotFoundException, Exception {
		
		BeanFactory factory = getBeanFactory(extendedBeanDeclaration);
		 
		factory.initBean(beanInstance, extendedBeanDeclaration);
		return beanInstance;
	}
	
	public static Class<?> getBeanClass(ExtendedBeanDeclaration beanDeclaration)
			throws ClassNotFoundException {
	
		Class<?> beanClazz = null;
		String beanClassName = beanDeclaration.getBeanClassName();
		if (!Strings.isNullOrEmpty(beanClassName)) {
			beanClazz = ClassUtils.getClass(beanClassName);
		}
		if (null == beanClazz) {
			beanClazz = beanDeclaration.getDefaultBeanClass();
		}
		return beanClazz;
	}
	
	private synchronized static Map<String, Set<ExtendedBeanDeclaration>> loadBeanDeclarations(
			Class<?> beanClass) throws Exception {
		
		if (beanDeclarationsMap.containsKey(beanClass)) {
			return beanDeclarationsMap.get(beanClass);
		}
		Map<String, Set<ExtendedBeanDeclaration>> beanDeclarations = BeanDeclarationsLoader
				.loadBeanDeclarations(beanClass);
		beanDeclarationsMap.put(beanClass, beanDeclarations);
		return beanDeclarations;
	}

}
