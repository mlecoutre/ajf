package am.ajf.core.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import am.ajf.core.Service;
import am.ajf.core.logger.LoggerFactory;
import am.ajf.core.utils.BeanUtils;

import com.google.common.base.Throwables;

public class CacheManagerFactory {

	public static final String SIMPLE_CACHE_MANAGER = "simple";
	
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

				BeanUtils.initialize(cacheManager);
				if (cacheManager instanceof Service) {
					((Service) cacheManager).start();
				}
								
				logger.info("Find CacheManager impl {} registered as {}",
						cacheManager.getClass().getName(),
						cacheManager.getName());

				// register the CacheManager impl
				cacheManagersMap.put(cacheManager.getName(),
						cacheManager);
				// is it the first CacheManager impl ?
				if (null == firstCacheManager) {
					firstCacheManager = cacheManager;
					logger.info(
							"Set CacheManager '{}' as default CacheManager",
							cacheManager.getName());
				}
			}
			catch (Throwable e) {
				Throwable t = Throwables.getRootCause(e);
				logger.warn("Unable to load ".concat(t.getMessage()));
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
	 * @param cacheManagerName
	 * @return
	 * @throws NullPointerException
	 */
	public static CacheManager getCacheManager(String cacheManagerName)
			throws NullPointerException {

		if (!cacheManagersMap.containsKey(cacheManagerName))
			throw new NullPointerException("The CacheManager '".concat(
					cacheManagerName).concat("' can not be found."));

		CacheManager cacheManager = cacheManagersMap
				.get(cacheManagerName);
		return cacheManager;
	}

	/**
	 * return the CacheManager names
	 * 
	 * @return
	 */
	public static Set<String> getCacheManagerNames() {
		return cacheManagersMap.keySet();
	}

}
