package am.ajf.injection;

import am.ajf.core.busines.PolicyUtils;
import am.ajf.core.logger.LoggerFactory;
import am.ajf.core.services.ServiceUtils;
import am.ajf.core.utils.ClassUtils;
import am.ajf.web.WebUtils;
import java.io.Serializable;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.slf4j.Logger;

/**
 * Intercept @ErrorHandled annotation and create a generic error handling
 * 
 * @author E010925
 * 
 */
@Interceptor
@ErrorHandled
public class ErrorHandlingInterceptor implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory
			.getLogger(ErrorHandlingInterceptor.class);

	/**
	 * Default constructor
	 */
	public ErrorHandlingInterceptor() {
		super();
	}

	/**
	 * Manage Error Handling around @ErrorHandled annoted method
	 * 
	 * @param ctx
	 *            invocation context
	 * @return result of the normal call
	 * @throws Exception
	 *             depending of the type of class, throw BusinessLayerException,
	 *             ServiceException or the same exception on an unknown class
	 *             type.
	 */
	@AroundInvoke
	public Object manageErrorHandling(InvocationContext ctx) throws Exception {
		Object res = null;
		try {
			if (logger.isTraceEnabled()) {
				logger.trace(">> manageErrorHandling interceptor");
			}
			res = ctx.proceed();
			if (logger.isTraceEnabled()) {
				logger.trace("<< manageErrorHandling interceptor [OK]");
			}
		} catch (Exception exception) {
			Class<? extends Object> targetClass = ctx.getMethod()
					.getDeclaringClass();
			if (logger.isTraceEnabled()) {
				logger.trace(String.format(
						"<< manageErrorHandling interceptor [ERROR] in %s",
						ctx.getMethod()));
			}

			Logger targetLog = LoggerFactory.getLogger(targetClass);
			// Test type of class (Web, Policy, SERVICE)
			if (ClassUtils.isPolicyImpl(targetClass)) {
				PolicyUtils.handleError(exception, targetClass, targetLog);
			} else if (ClassUtils.isServiceImpl(targetClass)) {
				ServiceUtils.handleError(exception, targetClass, targetLog);
			} else if (ClassUtils.isWebController(targetClass)) {
				WebUtils.handleError(exception, targetLog,
						"@ErrorHandlingInterceptor on Web Controller");
			} else {
				// Should not happen, but trace it anyway
				handleUnknowClassTypeError(exception, targetClass, targetLog);
			}

		}
		return res;
	}

	/**
	 * Manage exception on an unknown type of class. This method log only in
	 * error the exception in the targetLog.
	 * 
	 * @param exception
	 *            the triggered exception
	 * @param targetClass
	 *            class where exception occurred
	 * @param targetLog
	 *            logger from the targetClass
	 * @throws Exception
	 *             the same exception.
	 */
	private void handleUnknowClassTypeError(Exception exception,
			Class<? extends Object> targetClass, Logger targetLog)
			throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String
					.format("ErrorHandlingInterceptor catch exception on unknown class type %s",
							targetClass));
		}

		targetLog.error(
				"ErrorHandlingInterceptor cacth Exception:"
						+ exception.getMessage(), exception);
		throw exception;
	}
}
