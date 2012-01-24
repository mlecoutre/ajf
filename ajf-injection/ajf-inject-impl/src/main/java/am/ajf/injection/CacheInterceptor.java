package am.ajf.injection;

import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;

import am.ajf.core.cache.Cache;
import am.ajf.core.logger.LoggerFactory;
import am.ajf.core.utils.BeanUtils;

@Cached
@Interceptor
public class CacheInterceptor {
	
	private final Logger logger = LoggerFactory.getLogger(CacheInterceptor.class);
	
	private boolean initialized = false;
	
	private Cached cachedAnnotation = null;
	private Cache cache = null;
	private KeyBuilder keyBuilder = null;
	
	
	public CacheInterceptor() {
		super();
	}
		
	@AroundInvoke
	public Object manageCache(InvocationContext ctx) throws Throwable {
		
		Object res = null;		
		
		// check init
		if (!initialized) {
			init(ctx);
		}
		
		// process key
		Object key = keyBuilder.build(cachedAnnotation, ctx.getParameters());
				
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
			cache.put(key, res);
			
			return res;
		}
		catch (Throwable e) {
			throw e;
		}
		
	}

	public synchronized void init(InvocationContext ctx) {
		
		if (initialized) 
			return;
		
		Class<?> targetClass = ctx.getTarget().getClass();
		Method method = ctx.getMethod();
		
		if (method.isAnnotationPresent(Cached.class)) {
			cachedAnnotation = method.getAnnotation(Cached.class);
		}
		else {
			if (targetClass.isAnnotationPresent(Cached.class)) {
				cachedAnnotation = targetClass.getAnnotation(Cached.class);
			}									
		}
		
		Class<?> cacheBuilderClass = cachedAnnotation.cacheBuilder();
		CacheBuilder cacheBuilder = (CacheBuilder) BeanUtils.newInstance(cacheBuilderClass);
		cache = cacheBuilder.build(targetClass, cachedAnnotation);
		if (cachedAnnotation.clearCache())
			cache.clear();
		
		Class<?> keyBuilderClass = cachedAnnotation.keyBuilder();
		keyBuilder = (KeyBuilder) BeanUtils.newInstance(keyBuilderClass);
		
		initialized = true;
	}
	
}
