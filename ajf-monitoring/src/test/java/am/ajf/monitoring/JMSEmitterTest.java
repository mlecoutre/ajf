package am.ajf.monitoring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;

import am.ajf.core.logger.LoggerFactory;
import am.ajf.monitoring.EventHandler;
import am.ajf.monitoring.EventsDomain;
import am.ajf.monitoring.impl.JMSEmitter;
import am.ajf.monitoring.impl.XmlJAXBFormatter;

public class JMSEmitterTest {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public JMSEmitterTest() {
		super();
	}

	@Test
	public void testSendJMSEvent() throws JAXBException, IOException, Exception {
		
		EventsDomain manager = new EventsDomain();
		JMSEmitter jmsEmitter = new JMSEmitter();
		EventHandler handler = new EventHandler(new XmlJAXBFormatter(
				MyEvent.class), jmsEmitter);
		manager.setDefaultEventHandler(handler);

		MyEvent event = new MyEvent("Albert", "Dupont");
		manager.fireEvent(event);

		Thread.sleep(500);
		
		manager.close();
		
		List<String> msgs = browseMessage(jmsEmitter.getConnectionFactoryName(), jmsEmitter.getDestinationName());
		Assert.assertTrue("The list of msgs shouldn't be null;", (msgs != null) && (!msgs.isEmpty()));

	}
	
	
	/**
	 * Only usable when the destination is a queue.
	 * @param connectionFactoryName 
	 * @param destinationName 
	 * 
	 * @return list of messages
	 */
	private List<String> browseMessage(String connectionFactoryName, String destinationName) {
		
		List<String> msgsStr = new ArrayList<String>();
		
		Connection connection = null;
		Session session = null;
		QueueBrowser browser = null;
		try {
			InitialContext ctx = new InitialContext();

			ConnectionFactory qcf = (ConnectionFactory) ctx
					.lookup(connectionFactoryName);

			Queue notifyQ = (Queue) ctx.lookup(destinationName);

			// Create connection, session, and sender.
			connection = qcf.createConnection();
			connection.start();
			session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			browser = session.createBrowser(notifyQ);

			@SuppressWarnings("unchecked")
			Enumeration<Message> msgs = browser.getEnumeration();
			if (!msgs.hasMoreElements()) {
				logger.warn("No messages in queue");
			} else {
				while (msgs.hasMoreElements()) {
					TextMessage tempMsg = (TextMessage) msgs.nextElement();
					msgsStr.add(tempMsg.getText());
					logger.debug("Message: " + tempMsg.getText());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			if (browser != null) {
				try {
					browser.close();
				} catch (JMSException e) {
				}
			}
			
			if (session != null) {
				try {
					session.close();
				} catch (JMSException e) {
				}
			}
			
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
				}
			}
		}

		return msgsStr;
	}

}
