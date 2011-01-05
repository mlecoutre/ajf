package ajf.utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;

import ajf.logger.LoggerFactory;
import ajf.utils.helpers.URLHelper;

/**
 * 
 * @author vincent claeysen
 * 
 */
public abstract class BeanUtils {
	
	private static final Logger logger = LoggerFactory.getLogger();
	
	private static boolean initialized = false;
	private static Object token = new Object();
	
	protected static Collection<BeanInitializer> initializers = new HashSet<BeanInitializer>();
	
	/**
	 * register a new BeanInitializer
	 * @param beanInitializer
	 */
	protected static void registerBeanInitializer(BeanInitializer beanInitializer) {
		if (null != beanInitializer) {
			if (!initializers.contains(beanInitializer))
				initializers.add(beanInitializer);
		}
	}
	
	/**
	 * unregister a new BeanInitializer
	 * @param beanInitializer
	 */
	protected static void unregisterBeanInitializer(BeanInitializer beanInitializer) {
		if (null != beanInitializer) {
			if (initializers.contains(beanInitializer))
				initializers.remove(beanInitializer);
		}
	}

	/**
	 * 
	 * @param clazz
	 * @return a Map of all public methods
	 */
	public static Map<String, Method> listMethodsAsMap(Class<?> clazz) {

		Map<String, Method> daoMethodsMap = new HashMap<String, Method>();
		Method[] methods = clazz.getMethods();
		if (null != methods) {
			for (Method method : methods) {
				if (!Object.class.equals(method.getDeclaringClass()))
					daoMethodsMap.put(method.getName(), method);
			}
		}
		return daoMethodsMap;
	}

	/**
	 * 
	 * @param clazz
	 * @return a new initialized instance
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T instanciate(Class<?> clazz)
			throws InstantiationException, IllegalAccessException {

		// call the default constructor
		Object bean = clazz.newInstance();
		
		// initialize bean
		initialize(bean);

		return (T) bean;

	}

	/**
	 * apply the collection of BeanInitializer on the given bean
	 * @param bean
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private static void initialize(Object bean) {
		
		if (!initialized) {
			synchronized (token) { 
				if (!initialized) {
					initialized = true;					
					
					try {
						ClassLoader cl = BeanUtils.class.getClassLoader();
						Enumeration<URL> componentsURL = cl.getResources("META-INF/ajf.utils.BeanInitializer");
					
						if (null != componentsURL) {
						
							while (componentsURL.hasMoreElements()) {
							
								URL url = componentsURL.nextElement();
								
								try {
									logger.info("Load the resource URL '" + url + "'.");
									String content = URLHelper.loadURLAsString(url, ";");
									
									if ((null != content) && (!content.isEmpty())) {
										String[] classeNames = content.split(";");
										for (int i = 0; i < classeNames.length; i++) {
											String clazzName = ("".concat(classeNames[i])).trim();											
											try {
												
												// load the defined class
												Class<?> clazz = cl.loadClass(clazzName);
												BeanInitializer beanInitializer = (BeanInitializer) clazz.newInstance();
												// get the default instance
												beanInitializer = beanInitializer.getInstance();
												logger.info("Register the BeanInitializer '" + clazzName + "'.");
												// add the BeanInitializer
												initializers.add(beanInitializer);
												
											} catch (ClassNotFoundException e) {
												logger.warn("Unable to load the BeanInitializer class '" + clazzName + "'.", e);
											} catch (InstantiationException e) {
												logger.warn("Unable to instanciate the BeanInitializer class '" + clazzName + "'.", e);
											} catch (IllegalAccessException e) {
												logger.warn("Unable to access the BeanInitializer constructor for class '" + clazzName + "'.", e);
											}
										}
									}
								} catch (IOException e) {
									logger.warn("Unable to load the resource URL '" + url + "'.", e);
								}
								
								
							}
						}
					} catch (IOException e) {
						logger.warn("Unable to get resources 'META-INF/ajf.utils.BeanInitializer'.", e);
					}
				}
			}
		}
		
		if (!initializers.isEmpty()) {
			for (BeanInitializer initializer : initializers) {
				initializer.initialize(bean);
			}
		}
		
	}

	/**
	 * 
	 * @return the caller class name
	 */
	public static String getClassName() {
		/* populate the stack trace */
		StackTraceElement[] stack = new Throwable().fillInStackTrace()
				.getStackTrace();
		/* get the caller class name */
		String callerClassName = stack[1].getClassName();
		return callerClassName;
	}

	/**
	 * 
	 * @return the caller method name
	 */
	public static String getMethodName() {
		/* populate the stack trace */
		StackTraceElement[] stack = new Throwable().fillInStackTrace()
				.getStackTrace();
		/* get the caller class name */
		String callerMethodName = stack[1].getMethodName();
		return callerMethodName;
	}

}
