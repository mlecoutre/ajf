package am.ajf.core.logger;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.MDC;

public class LoggerFactoryTest {

	private final Logger logger = LoggerFactory.getLogger(LoggerFactoryTest.class);
	
	public LoggerFactoryTest() {
		super();
	}
	
	@Test
	public void testSimpleLogging() {
		logger.info("Just a test");
	}
	
	@Test
	public void testLoggingWithMDC() {
		MDC.put("userId", "u002617");
		logger.info("Just a test");
	}
	
	
}
