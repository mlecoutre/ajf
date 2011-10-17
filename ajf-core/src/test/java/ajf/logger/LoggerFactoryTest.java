package ajf.logger;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.MDC;

public class LoggerFactoryTest {

	@Test
	public void testLogging() {
		
		Logger logger = LoggerFactory.getLogger();
		
		MDC.put("userId", "u002617");
		
		logger.info("Just a test");
		
	}
	
}
