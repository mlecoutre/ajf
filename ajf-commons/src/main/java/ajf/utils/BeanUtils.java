package ajf.utils;

import java.util.ServiceLoader;

import org.slf4j.Logger;

import ajf.logger.LoggerFactory;

/**
 * 
 * @author vincent claeysen
 * 
 */
public class BeanUtils {
		
	private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);
	
	private static final BeanUtils instance = new BeanUtils(); 
	
	private boolean initialized = false;
	private Object token = new Object();
	
	private BeanUtilsDelegate beanUtilsDelegate = null;
		
	private BeanUtils() {
		super();
	}
	
	public static BeanUtils getInstance() {
		return instance;
	}
	
	public void setBeanUtilsDelegate(BeanUtilsDelegate beanUtilsDelegate) {
		logger.info("set BeanUtilsDelegate to instance of: ".concat(beanUtilsDelegate.getClass().getName()));
		this.beanUtilsDelegate = beanUtilsDelegate;
	}

	/**
	 * 
	 * @param clazz
	 * @return a new initialized instance
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public <T> T newInstance(Class<?> clazz)
			throws RuntimeException {
		
		checkInit();

		// call the default constructor
		Object newBeanInstance = null;
		try {
			if (null != beanUtilsDelegate) 
				newBeanInstance = beanUtilsDelegate.instanciate(clazz);
		}
		catch (Exception e) {
			logger.error("Exception while trying to instanciate bean with BeanUtilsDelegate: ".concat(beanUtilsDelegate.getClass().getName()), e);
		}		
		try {
			if (null == newBeanInstance) {
				newBeanInstance = clazz.newInstance();		
				// initialize bean
				if (null != beanUtilsDelegate)
					beanUtilsDelegate.initialize(newBeanInstance);
			}
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
	 */
	private void checkInit() {
		
		if (!initialized) {
			synchronized (token) { 
				if (!initialized) {
					initialized = true;
					try {
						ServiceLoader<BeanUtilsDelegate> budLoader = ServiceLoader.load(BeanUtilsDelegate.class);
						if (null != budLoader) {
							for (BeanUtilsDelegate bud : budLoader) {
								logger.info("Use BeanUtilsDelegate instance of: ".concat(bud.getClass().getName()));
								this.beanUtilsDelegate = bud;
							}
						}
					} catch (Exception e) {
						logger.warn("Unable to get resources 'META-INF/services/ajf.utils.BeanUtilsDelegate'.", e);
					}
				}
			}
		}
		
	}
	
	public void reset() {
		
		synchronized (token) { 
			initialized = false;
		}
	}

}
