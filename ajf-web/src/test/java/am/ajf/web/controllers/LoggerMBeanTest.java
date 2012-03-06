package am.ajf.web.controllers;

import org.junit.Assert;
import org.junit.Test;

/**
 * LoggerMBeanTest
 * 
 * @author E010925
 * 
 */
public class LoggerMBeanTest {

	private final LoggerMBean loggerMBean = new LoggerMBean();

	/**
	 * testLoggerInit
	 */
	@Test
	public void testLoggerInit() {
		loggerMBean.init();
		Assert.assertTrue("Logger List should not be null",
				(loggerMBean.getLoggers() != null)
						&& (loggerMBean.getLoggers().size() > 0));
	}
}
