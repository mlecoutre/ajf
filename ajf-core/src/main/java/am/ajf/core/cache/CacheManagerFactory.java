package am.ajf.core.cache;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import am.ajf.core.logger.LoggerFactory;
import am.ajf.core.utils.BeanUtils;

public class CacheManagerFactory {

	private final static Logger logger = LoggerFactory
			.getLogger(CacheManagerFactory.class);

	private static CacheManager firstCacheManager = null;
	private static Map<String, CacheManager> cacheManagersMap = new ConcurrentHashMap<String, CacheManager>();

	static {
		initClass();
	}

	/**
	 * init impls map
	 */
	private static void initClass() {

		firstCacheManager = null;

		ServiceLoader<CacheManager> loader = ServiceLoader
				.load(CacheManager.class);

		for (Iterator<CacheManager> iterator = loader.iterator(); iterator
				.hasNext();) {
			try {
				CacheManager cacheManager = iterator.next();
				
				logger.info("Find CacheManager impl {} registered as {}", cacheManager.getClass()
						.getName(), cacheManager.getProviderName());
				BeanUtils.initialize(cacheManager);
				cacheManager.start();
				
				// register the CacheManager impl
				cacheManagersMap.put(cacheManager.getProviderName(),
						cacheManager);
				// is it the first CacheManager impl ?
				if (null == firstCacheManager) {
					firstCacheManager = cacheManager;
					logger.info("Set CacheManager '{}' as default CacheManager", cacheManager.getProviderName());
				}
			}
			catch (Throwable e) {
				logger.info(e.getMessage(), e);
			}
		}
	}

	private CacheManagerFactory() {
		super();
	}

	/**
	 * return the first finded CacheManager impl
	 * 
	 * @return
	 * @throws NullPointerException
	 */
	public static CacheManager getFirstCacheManager()
			throws NullPointerException {
		
		if (null == firstCacheManager)
			throw new NullPointerException(
					"There is no registerd CacheManager impl.");
		
		return firstCacheManager;
	}

	/**
	 * return a specific CacheManager
	 *  
	 * @param cacheManagerSimpleClassName
	 * @return
	 * @throws NullPointerException
	 */
	public static CacheManager getCacheManager(String cacheManagerProviderName)
			throws NullPointerException {

		if (!cacheManagersMap.containsKey(cacheManagerProviderName))
			throw new NullPointerException("The CacheManager '".concat(
					cacheManagerProviderName).concat("' can not be found."));

		CacheManager cacheManager = cacheManagersMap
				.get(cacheManagerProviderName);
		return cacheManager;
	}
	
	/**
	 * return the CacheManager names
	 * 
	 * @return
	 */
	public static Collection<String> getCacheManagerNames() {
		return cacheManagersMap.keySet();			 
	}

}
