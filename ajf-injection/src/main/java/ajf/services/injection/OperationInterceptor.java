package ajf.services.injection;

import java.util.Arrays;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;

import ajf.logger.LoggerFactory;

public class OperationInterceptor implements MethodInterceptor {

	private final static String BASE = "ajf.monitoring.";

	public OperationInterceptor() {
		super();
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {

		String serviceName = invocation.getMethod().getDeclaringClass()
				.getSimpleName();
		String methodName = invocation.getMethod().getName();
		String category = BASE.concat(serviceName).concat(".")
				.concat(methodName);
		Logger logger = LoggerFactory.getLogger(category);

		if (logger.isTraceEnabled()) {

			String args = Arrays.toString(invocation.getArguments());

			long start = System.currentTimeMillis();
			logger.trace(String.format(
					"Start invocation of method %s() from class %s.",
					methodName, serviceName));

			Object res = null;
			Throwable throwed = null;
			try {
				res = invocation.proceed();
			}
			catch (Throwable t) {
				throwed = t;
			}

			long end = System.currentTimeMillis();

			logger.trace(String.format(
					"End invocation of method %s() from class %s.", methodName,
					serviceName));

			logger.trace(String
					.format("Invocation of method %s() from class %s with parameters %s took %s ms.",
							methodName, serviceName, args,
							String.valueOf(end - start)));
			
			if (null != throwed)
				throw throwed;
			return res;
			
		}
		else {

			return invocation.proceed();

		}

	}

}
