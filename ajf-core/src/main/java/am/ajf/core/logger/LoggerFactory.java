package am.ajf.core.logger;

import org.slf4j.Logger;

import am.ajf.core.ApplicationContext;
import am.ajf.core.utils.ClassUtils;
import ch.qos.logback.classic.LoggerContext;

public class LoggerFactory {
	
	static {
		
		// launch the ApplicationContext
		ApplicationContext.init();
		
		LoggerContext ctx = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
		if (ctx.isStarted()) 
			ctx.stop();
		ctx.start();
				
		Logger logger = org.slf4j.LoggerFactory.getLogger(LoggerFactory.class);
		try {
			ClassUtils.loadClass("am.ajf.core.logger.JulToSLF4jHandlerBridge");
		}
		catch (Throwable e) {
			logger.warn("The handler bridge for 'java.utils.logging' can not be installed.");
		}
				
		logger.info("LoggerFactory initialized.");
		
	}
	
	private LoggerFactory() {
		super();
	}

	/**
	 * 
	 * @return the appropriate logger 
	 */
	/**
	public static Logger getLogger() {
		
		// populate the stack trace 
		StackTraceElement[] stack = new Throwable().fillInStackTrace().getStackTrace();
		// get the caller class name 
		String callerClassName = stack[1].getClassName();
		return getLogger(callerClassName);
		
	}
	**/

	/**
	 * 
	 * @param name
	 * @return the appropriate logger
	 */
	public static Logger getLogger(String name) {
		/* get the corresponding logger */
		Logger logger = org.slf4j.LoggerFactory.getLogger(name);
		return logger;
	}
	
	/**
	 * 
	 * @param clazz
	 * @return the appropriate logger
	 */
	public static Logger getLogger(Class<?> clazz) {
		/* get the corresponding logger */
		Logger logger = org.slf4j.LoggerFactory.getLogger(clazz);
		return logger;
	}
	
	/**
	 * 
	 * @return
	 */
	public static LoggerContext getDelegate() {
		LoggerContext ctx = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
		return ctx;
	}
	
}
