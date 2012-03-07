package am.ajf.injection;

import am.ajf.core.logger.LoggerFactory;
import am.ajf.core.utils.ClassUtils;
import java.io.Serializable;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.slf4j.Logger;

/**
 * MonitoringInterceptor. Push elasped time for the method invocation in the
 * logs.
 * 
 * @author U002617
 * 
 */
@Monitored
@Interceptor
public class MonitoringInterceptor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory
			.getLogger(MonitoringInterceptor.class);

	/**
	 * Default constructor
	 */
	public MonitoringInterceptor() {
		super();
	}

	/**
	 * Intercept monitored classes or method and trace it on log system
	 * 
	 * @param ctx
	 *            invocation context
	 * @return object
	 * @throws Throwable
	 *             on error
	 */
	@AroundInvoke
	public Object monitor(InvocationContext ctx) throws Throwable {

		Class<? extends Object> targetClass = ctx.getTarget().getClass();
		String serviceName = ClassUtils
				.processServiceInterfaceName(targetClass);
		String operation = ctx.getMethod().getName();

		logger.trace(">> ".concat(serviceName).concat("#").concat(operation)
				.concat("(...)"));
		long start = System.currentTimeMillis();

		try {
			Object res = ctx.proceed();
			return res;
		} finally {
			long end = System.currentTimeMillis();
			long ellapsed = end - start;
			logger.trace("<< ".concat(serviceName).concat("#")
					.concat(operation).concat("(...) in {} ms."), ellapsed);
		}
	}

}
