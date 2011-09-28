package ajf.logger;

import org.slf4j.Logger;

import ch.qos.logback.classic.LoggerContext;



public abstract class LoggerFactory {

	/**
	 * 
	 * @return the appropriate logger 
	 */
	public static Logger getLogger() {
		
		/* populate the stack trace */
		StackTraceElement[] stack = new Throwable().fillInStackTrace().getStackTrace();
		/* get the caller class name */
		String callerClassName = stack[1].getClassName();
		return getLogger(callerClassName);
		
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static Logger getLogger(String name) {
		/* get the corresponding logger */
		Logger logger = org.slf4j.LoggerFactory.getLogger(name);
		return logger;
	}
	
	/**
	 * 
	 * @param clazz
	 * @return
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
