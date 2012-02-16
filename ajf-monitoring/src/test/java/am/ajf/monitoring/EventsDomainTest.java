package am.ajf.monitoring;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;

import am.ajf.core.logger.LoggerFactory;
import am.ajf.monitoring.impl.ConsoleEmitter;
import am.ajf.monitoring.impl.XmlJAXBFormatter;

public class EventsDomainTest {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(EventsDomainTest.class);
	
	@Test
	public void testSendSimpleEvent() throws JAXBException, IOException,
			InterruptedException {

		// Given
		
		EventsDomain evtsDomain = new EventsDomain();
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(outputStream, true);
		EventEmitter emitter = new ConsoleEmitter(printStream);
		
		EventHandler handler = new EventHandler(new XmlJAXBFormatter(
				MyEvent.class), emitter);
		evtsDomain.setDefaultEventHandler(handler);

		MyEvent event = new MyEvent("Albert", "Dupont");
		
		// When
		
		evtsDomain.sendEvent(event);
		
		// Then

		// wait some time
		Thread.sleep(100);
		
		evtsDomain.close();
		
		printStream.flush();

		String utf8 = Charset.forName("UTF-8").displayName();
		String res = outputStream.toString(utf8);

		printStream.close();
		outputStream.close();

		Assert.assertTrue("The message has not been published.", (null != res)
				&& (0 < res.length()));

	}

	@Test
	public void testSendRegisteredEvent() throws JAXBException, IOException, InterruptedException {
		
		EventsDomain evtsDomain = new EventsDomain();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(outputStream, true);
		EventEmitter emitter = new ConsoleEmitter(printStream);

		EventHandler handler = new EventHandler(new XmlJAXBFormatter(
				MyEvent.class), emitter);
		evtsDomain.setDefaultEventHandler(handler);

		String eventType = "event/test";
		EventFactory.registerEvent(eventType, MyEvent.class);

		MyEvent event = EventFactory.newEvent(eventType);
		event.setFirstName("Bob");
		event.setLastName("Durand");

		evtsDomain.sendEvent(event);

		// wait some time
		Thread.sleep(100);
		
		evtsDomain.close();
		
		printStream.flush();

		String utf8 = Charset.forName("UTF-8").displayName();
		String res = outputStream.toString(utf8);

		printStream.close();
		outputStream.close();

		Assert.assertTrue("The message has not been published.", (null != res)
				&& (0 < res.length()));

	}
	
}
