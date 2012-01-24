package am.ajf.core.utils;

import java.util.ServiceLoader;

import org.slf4j.Logger;

import am.ajf.core.logger.LoggerFactory;

/**
 * 
 * @author vincent claeysen
 * 
 */
public class BeanUtils {
		
	private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);
	
	private static boolean initialized = false;
	private static final Object token = new Object();
	
	private static BeanFactory beanUtilsDelegate = null;
		
	private BeanUtils() {
		super();
	}
	
	public synchronized static void setBeanUtilsDelegate(BeanFactory delegate) {
		logger.info("set BeanUtilsDelegate to instance of: ".concat(delegate.getClass().getName()));
		beanUtilsDelegate = delegate;
	}
	
	public static BeanFactory getBeanUtilsDelegate() {
		checkInit();
		return beanUtilsDelegate;
	}

	/**
	 * 
	 * @param clazz
	 * @return a new initialized instance
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> T newInstance(Class<T> clazz)
			throws RuntimeException {
		
		checkInit();

		// call the default constructor
		T newBeanInstance = null;
		try {
			if (null != beanUtilsDelegate) {
				newBeanInstance = beanUtilsDelegate.instanciate(clazz);
				if (null != newBeanInstance)
					return newBeanInstance;
			}
		}
		catch (Exception e) {
			logger.error("Exception while trying to instanciate bean with BeanUtilsDelegate: ".concat(beanUtilsDelegate.getClass().getName()), e);
		}	
		
		try {
			newBeanInstance = clazz.newInstance();		
			// initialize bean
			if (null != beanUtilsDelegate)
				beanUtilsDelegate.initialize(newBeanInstance);
		
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			logger.error("Exception while trying to instanciate/initialize bean: ".concat(clazz.getName()), e);
			throw new RuntimeException(e);
		}

		return (T) newBeanInstance;

	}
	
	/**
	 * 
	 * @param beanInstance
	 */
	public static <T> void initialize(T beanInstance) {
		
		checkInit();
		
		if (null == beanUtilsDelegate)
			return;
		beanUtilsDelegate.initialize(beanInstance);
		
	}
	
	/**
	 * 
	 */
	private static void checkInit() {
		
		if (!initialized) {
			synchronized (token) { 
				if (!initialized) {
					try {
						ServiceLoader<BeanFactory> budLoader = ServiceLoader.load(BeanFactory.class);
						if (null != budLoader) {
							for (BeanFactory bud : budLoader) {
								logger.info("Use BeanUtilsDelegate instance of: ".concat(bud.getClass().getName()));
								beanUtilsDelegate = bud;
								break;
							}
						}
					} catch (Exception e) {
						logger.warn("Unable to get resources 'META-INF/services/".concat(BeanFactory.class.getName()).concat("'."), e);
					}
					initialized = true;
				}
			}
		}
		
	}
	
	public static void terminate() {
		
		synchronized (token) { 
			initialized = false;
		
			if (null != beanUtilsDelegate)
				beanUtilsDelegate.terminate();
			beanUtilsDelegate = null;
		}
		
	}

}
