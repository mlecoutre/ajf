package ajf.monitoring;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;

import ajf.logger.LoggerFactory;
import ajf.monitoring.impl.ConsoleEmitter;
import ajf.monitoring.impl.XmlJAXBFormatter;

public class EventManagerTest {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger();

	@Test
	public void testSendSimpleEvent() throws JAXBException, IOException,
			InterruptedException {

		EventManager manager = new EventManager();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(outputStream, true);
		EventEmitter emitter = new ConsoleEmitter(printStream);

		EventHandler handler = new EventHandler(new XmlJAXBFormatter(
				MyEvent.class), emitter);
		manager.setDefaultEventHandler(handler);

		MyEvent event = new MyEvent("Albert", "Dupont");
		manager.sendEvent(event);

		manager.close();

		// wait some time
		Thread.sleep(100);

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

		EventManager manager = new EventManager();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(outputStream, true);
		EventEmitter emitter = new ConsoleEmitter(printStream);

		EventHandler handler = new EventHandler(new XmlJAXBFormatter(
				MyEvent.class), emitter);
		manager.setDefaultEventHandler(handler);

		String eventType = "event/test";
		EventFactory.registerEvent(eventType, MyEvent.class);

		MyEvent event = EventFactory.newEvent(eventType);
		event.setFirstName("Bob");
		event.setLastName("Durand");

		manager.sendEvent(event);

		manager.close();

		// wait some time
		Thread.sleep(100);

		printStream.flush();

		String utf8 = Charset.forName("UTF-8").displayName();
		String res = outputStream.toString(utf8);

		printStream.close();
		outputStream.close();

		Assert.assertTrue("The message has not been published.", (null != res)
				&& (0 < res.length()));

	}

}
