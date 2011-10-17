package ajf.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;

import ajf.logger.LoggerFactory;

/**
 * 
 * @author vincent claeysen
 * 
 */
public class BeanUtils {
		
	private final static Logger logger = LoggerFactory.getLogger();
	private final static BeanUtils instance = new BeanUtils(); 
	
	private boolean initialized = false;
	private Object token = new Object();
	
	private BeanInstanciator instanciator = null;
	protected List<BeanInitializer> biList = new ArrayList<BeanInitializer>();
	
	private BeanUtils() {
		super();
	}
	
	public static BeanUtils getInstance() {
		return instance;
	}
	
	public void setBeanInstanciator(BeanInstanciator beanInstanciator) {
		logger.info("set BeanInstanciator to instance of: ".concat(beanInstanciator.getClass().getName()));
		instanciator = beanInstanciator;
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
			throws InstantiationException, IllegalAccessException {
		
		checkInit();

		// call the default constructor
		Object bean = null;
		if (null != instanciator) {
			bean = instanciator.instanciate(clazz);
		}
		else {
			bean = clazz.newInstance();
		}
				
		// initialize bean
		initializeBean(bean);

		return (T) bean;

	}
	
	private void checkInit() {
		
		if (!initialized) {
			synchronized (token) { 
				if (!initialized) {
					initialized = true;
					try {
						ServiceLoader<BeanInitializer> biLoader = ServiceLoader.load(BeanInitializer.class);
						if (null != biLoader) {
							for (BeanInitializer beanInitializer : biLoader) {
								logger.info("Use BeanInitializer instance of: ".concat(beanInitializer.getClass().getName()));
								biList.add(beanInitializer);
							}
						}
					} catch (Exception e) {
						logger.warn("Unable to get resources 'META-INF/services/ajf.utils.BeanInitializer'.", e);
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

	/**
	 * apply the collection of BeanInitializer on the given bean
	 * @param bean
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private void initializeBean(Object bean) {
		
		if ((null != biList) && (!biList.isEmpty())) {
			for (BeanInitializer beanInitializer : biList) {
				beanInitializer.initialize(bean);
			}
		}
		
	}

	

}
