package am.ajf.injection;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;

import am.ajf.core.logger.LoggerFactory;

@Monitored
@Interceptor
public class MonitoringInterceptor {
	
	private final Logger logger = LoggerFactory.getLogger(MonitoringInterceptor.class);

	public MonitoringInterceptor() {
		super();
	}
	
	@AroundInvoke
	public Object invoke(InvocationContext ic) throws Exception {
		logger.debug("In interceptor.");
		Object res = ic.proceed();
		return res;
	}
	
}
