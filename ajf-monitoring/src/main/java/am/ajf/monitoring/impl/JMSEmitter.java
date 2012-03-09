package am.ajf.monitoring.impl;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.slf4j.Logger;

import am.ajf.core.logger.LoggerFactory;
import am.ajf.monitoring.AbstractEvent;
import am.ajf.monitoring.api.EventEmitter;
import am.ajf.monitoring.exceptions.EventEmitterException;

/**
 * Allow to send events on a JMS destination. This destination can be either a
 * Queue or a Topic.
 * 
 * @author E010925
 * 
 */
public class JMSEmitter implements EventEmitter {

	public static final String JNDI_DEFAULT_EVENT_DEST = "AJFEventTarget";
	public static final String JNDI_DEFAULT_CONNECTION_FACTORY = "MonitoringConnectionFactory";

	private String connectionFactoryName = JNDI_DEFAULT_CONNECTION_FACTORY;
	private String destinationName = JNDI_DEFAULT_EVENT_DEST;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ConnectionFactory qcf = null;

	public JMSEmitter() {
		super();
	}
	
	public JMSEmitter(String connectionFactoryName, String destinationName) {
		this.connectionFactoryName = connectionFactoryName;
		this.destinationName = destinationName;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public String getConnectionFactoryName() {
		return connectionFactoryName;
	}

	public void setConnectionFactoryName(String connectionFactoryName) {
		this.connectionFactoryName = connectionFactoryName;
	}

	@Override
	public void send(AbstractEvent eventSource, String formattedEvent)
			throws EventEmitterException {
		
		Connection queue_conn = null;
		Session queue_session = null;
		MessageProducer queue_sender = null;
		
		try {

			InitialContext ctx = new InitialContext();

			if (null == qcf) {
				qcf = (ConnectionFactory) ctx	
					.lookup(connectionFactoryName);
			}

			Destination notifyQ = (Destination) ctx.lookup(destinationName);

			// Create connection, session, and sender.
			queue_conn = qcf.createConnection();
			queue_conn.start();
			queue_session = queue_conn.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			queue_sender = queue_session
					.createProducer(notifyQ);

			// Create the message and send.
			TextMessage message = queue_session.createTextMessage();
			message.setText(formattedEvent);
			queue_sender.send(message);
			
		} catch (Exception e) {
			logger.error("ERROR while sending the event.", e);
			throw new EventEmitterException(e);
		} finally {
			
			if (queue_sender != null) {
				try {
					queue_sender.close();
				} catch (JMSException e) {
				}
			}
			
			if (queue_session != null) {
				try {
					queue_session.close();
				} catch (JMSException e) {
				}
			}
			
			if (queue_conn != null) {
				try {
					queue_conn.close();
				} catch (JMSException e) {
				}
			}
		}

	}
	
}
