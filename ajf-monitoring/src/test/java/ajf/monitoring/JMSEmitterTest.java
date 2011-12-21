package ajf.monitoring;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;

import ajf.monitoring.impl.JMSEmitter;
import ajf.monitoring.impl.XmlJAXBFormatter;

public class JMSEmitterTest {

	@Test
	public void sendJMSEvent() throws JAXBException, IOException, Exception {
		EventManager manager = new EventManager();
		EventEmitter emitter = new JMSEmitter();
		EventHandler handler = new EventHandler(new XmlJAXBFormatter(
				MyEvent.class), emitter);
		manager.setDefaultEventHandler(handler);

		MyEvent event = new MyEvent("Albert", "Dupont");
		manager.sendEvent(event);

		manager.close();

		Thread.sleep(200);
		List<String> msgs = ((JMSEmitter) emitter).browseMessage();
		Assert.assertTrue("The list of msgs shouldn't be null;", msgs != null);

	}

}
