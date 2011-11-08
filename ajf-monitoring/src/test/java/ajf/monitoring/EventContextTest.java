package ajf.monitoring;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.slf4j.Logger;

import ajf.logger.LoggerFactory;
import ajf.monitoring.impl.ConsoleEmitter;
import ajf.monitoring.impl.XmlJAXBFormatter;

public class EventContextTest {

	private static final Logger logger = LoggerFactory.getLogger();

	@Test
	public void testSendEvent() throws JAXBException, IOException {

		EventHandler handler = new EventHandler(null, new XmlJAXBFormatter(
				MyEvent.class), new ConsoleEmitter());

		EventContext.getInstance().installDefaultEventHandler(handler);

		for (int i = 0; i<1000; i++) {
			EventContext.getInstance()
				.sendEvent(new MyEvent("Vincent", "Claeysen id#".concat(String.valueOf(i))));
		}

		EventContext.getInstance().close();
		
		logger.info("Done.");

	}

}
