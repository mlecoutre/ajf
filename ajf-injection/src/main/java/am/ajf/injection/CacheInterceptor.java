package am.ajf.injection;

import am.ajf.core.cache.Cache;
import am.ajf.core.logger.LoggerFactory;
import am.ajf.core.utils.BeanUtils;
import am.ajf.injection.annotation.Cached;
import am.ajf.injection.api.CacheBuilder;
import am.ajf.injection.api.KeyBuilder;

import java.io.Serializable;
import java.lang.reflect.Method;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.slf4j.Logger;

import com.google.common.base.Strings;

/**
 * CacheInterceptor. Deliver cache value on a Cached annotated class or method.
 * 
 * @author U002617
 * 
 */
@Cached
@Interceptor
public class CacheInterceptor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Logger logger = LoggerFactory
			.getLogger(CacheInterceptor.class);

	private boolean initialized = false;

	private Cached cachedAnnotation = null;
	private Cache cache = null;
	private KeyBuilder keyBuilder = null;
	private String defaultKey = null;

	/**
	 * Default constructor
	 */
	public CacheInterceptor() {
		super();
	}

	/**
	 * manageCache handling
	 * 
	 * @param ctx
	 *            the invocation context
	 * @return cache value
	 * @throws Throwable
	 *             on error
	 */
	@AroundInvoke
	public Object manageCache(InvocationContext ctx) throws Throwable {

		Object res = null;

		// check init
		if (!initialized) {
			init(ctx);
		}

		Class<?> targetClass = ctx.getTarget().getClass();
		Method targetMethod = ctx.getMethod();

		// process key
		Object key = keyBuilder.build(targetClass, targetMethod,
				cachedAnnotation, ctx.getParameters());
		if ((null == key)) {
			key = defaultKey;
		}

		// lookup in cache
		if (cache.exist(key)) {
			logger.debug("Return cached result for key: {}", key);
			res = cache.get(key);
			return res;
		}

		try {
			// invoke the process
			res = ctx.proceed();
			// store result Object in cache
			logger.debug("Store result in cache with key: {}", key);
			if (null != res)
				cache.put(key, res);

			return res;
		} catch (Throwable e) {
			throw e;
		}

	}

	/**
	 * Cache initialization regarding if cache annotation has been pushed on the
	 * class or on the method.
	 * 
	 * @param ctx
	 *            the invocation context
	 */
	public synchronized void init(InvocationContext ctx) {

		if (initialized) {
			return;
		}

		Class<?> targetClass = ctx.getTarget().getClass();
		Method targetMethod = ctx.getMethod();

		if (targetMethod.isAnnotationPresent(Cached.class)) {
			cachedAnnotation = targetMethod.getAnnotation(Cached.class);
		} else {
			if (targetClass.isAnnotationPresent(Cached.class)) {
				cachedAnnotation = targetClass.getAnnotation(Cached.class);
			}
		}

		Class<?> cacheBuilderClass = cachedAnnotation.cacheBuilder();
		CacheBuilder cacheBuilder = (CacheBuilder) BeanUtils
				.newInstance(cacheBuilderClass);
		cache = cacheBuilder.build(targetClass, targetMethod, cachedAnnotation);
		if (cachedAnnotation.clearCache()) {
			cache.clear();
		}

		Class<?> keyBuilderClass = cachedAnnotation.keyBuilder();
		keyBuilder = (KeyBuilder) BeanUtils.newInstance(keyBuilderClass);

		defaultKey = cachedAnnotation.defaultKey();
		if (Strings.isNullOrEmpty(defaultKey)) {
			defaultKey = targetMethod.toGenericString();
		}
		
		initialized = true;
	}

}
