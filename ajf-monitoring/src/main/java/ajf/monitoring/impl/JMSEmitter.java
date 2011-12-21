package ajf.monitoring.impl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.slf4j.Logger;

import ajf.logger.LoggerFactory;
import ajf.monitoring.AbstractEvent;
import ajf.monitoring.EventEmitter;
import ajf.monitoring.exceptions.EventEmitterException;

/**
 * Allow to send events on a JMS destination. This destination can be either a
 * Queue or a Topic.
 * 
 * @author E010925
 * 
 */
public class JMSEmitter implements EventEmitter {

	public static final String JNDI_DEFAULT_AJF_EVENT_DEST = "AJFEventQueue";
	public static final String JNDI_DEFAULT_CONNECTION_FACTORY = "queueConnectionFactory";

	private String destinationName = JNDI_DEFAULT_AJF_EVENT_DEST;
	private String connectionFactory = JNDI_DEFAULT_CONNECTION_FACTORY;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public JMSEmitter() {

	}

	public JMSEmitter(String destinationName, String connectionFactory) {
		this.destinationName = destinationName;
		this.connectionFactory = connectionFactory;
	}

	@Override
	public void send(AbstractEvent eventSource, String formattedEvent)
			throws EventEmitterException {
		Connection queue_conn = null;
		try {

			InitialContext ctx = new InitialContext();

			ConnectionFactory qcf = (ConnectionFactory) ctx
					.lookup(connectionFactory);

			Destination notifyQ = (Destination) ctx.lookup(destinationName);

			// Create connection, session, and sender.
			queue_conn = qcf.createConnection();

			Session queue_session = queue_conn.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			MessageProducer queue_sender = queue_session
					.createProducer(notifyQ);

			// Create the message and send.
			TextMessage message = queue_session.createTextMessage();
			message.setText(formattedEvent);
			queue_sender.send(message);

		} catch (Exception e) {
			logger.error("ERROR when sending the event.", e);
			throw new EventEmitterException(e);
		} finally {
			if (queue_conn != null) {
				try {
					queue_conn.close();
				} catch (JMSException e) {
				}
			}
		}

	}

	/**
	 * Only usable when the destination is a queue.
	 * 
	 * @return list of messages
	 */
	public List<String> browseMessage() {
		Connection connection = null;
		List<String> msgsStr = new ArrayList<String>();
		try {
			InitialContext ctx = new InitialContext();

			ConnectionFactory qcf = (ConnectionFactory) ctx
					.lookup(connectionFactory);

			Queue notifyQ = (Queue) ctx.lookup(destinationName);

			// Create connection, session, and sender.
			connection = qcf.createConnection();
			connection.start();
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);

			QueueBrowser browser = session.createBrowser(notifyQ);

			@SuppressWarnings("unchecked")
			Enumeration<Message> msgs = browser.getEnumeration();
			if (!msgs.hasMoreElements()) {
				logger.warn("No messages in queue");
			} else {
				while (msgs.hasMoreElements()) {
					Message tempMsg = (Message) msgs.nextElement();
					msgsStr.add(tempMsg.toString());
					logger.debug("Message: " + tempMsg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
