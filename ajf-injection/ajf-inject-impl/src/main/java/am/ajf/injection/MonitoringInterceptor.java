package am.ajf.injection;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Monitored
@Interceptor
public class MonitoringInterceptor {
	
	private final Logger logger = LoggerFactory.getLogger(MonitoringInterceptor.class);

	public MonitoringInterceptor() {
		super();
	}
	
	@AroundInvoke
	public Object manageTransaction(InvocationContext ctx) throws Throwable {
		logger.debug("In interceptor.");
		System.err.println("Intercepted");
		Object res = ctx.proceed();
		return res;
	}
	
}
