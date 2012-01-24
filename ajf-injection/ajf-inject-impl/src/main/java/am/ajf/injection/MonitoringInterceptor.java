package am.ajf.injection;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;

import am.ajf.core.logger.LoggerFactory;
import am.ajf.core.utils.ClassUtils;

@Monitored
@Interceptor
public class MonitoringInterceptor {
	
	private final Logger logger = LoggerFactory.getLogger(MonitoringInterceptor.class);

	public MonitoringInterceptor() {
		super();
	}
	
	@AroundInvoke
	public Object monitor(InvocationContext ctx) throws Throwable {
		
		Class<? extends Object> targetClass = ctx.getTarget().getClass();
		String serviceName = ClassUtils.processServiceInterfaceName(targetClass);
		String operation = ctx.getMethod().getName();
		
		logger.info(">> ".concat(serviceName).concat("#").concat(operation).concat("(...)"));
		long start = System.currentTimeMillis();
		
		try {
			Object res = ctx.proceed();
			return res;
		}
		finally {
			long end = System.currentTimeMillis();
			long ellapsed = end - start;
			logger.info("<< ".concat(serviceName).concat("#").concat(operation).concat("(...) in {} ms."), ellapsed);			
		}
	}
	
}
