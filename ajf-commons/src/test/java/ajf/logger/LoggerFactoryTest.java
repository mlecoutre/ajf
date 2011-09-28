package ajf.logger;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.slf4j.Logger;

import ch.qos.logback.classic.LoggerContext;

public class LoggerFactoryTest {

	@Test
	public void testLogging() {
		
		Log log = LogFactory.getLog("myLogger");
		log.info("Hello");
		
		Logger logger = LoggerFactory.getLogger();
		logger.info("Just a test");
		
		LoggerContext ctx = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
		List<ch.qos.logback.classic.Logger> loggersList = ctx.getLoggerList();
		for (ch.qos.logback.classic.Logger logger2 : loggersList) {
			System.out.println(logger2 + " " + logger2.getLevel());
		}
		
	}
	
}
