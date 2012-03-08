package am.ajf.core.mail;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

/**
 * Service for sending mails
 * 
 * @author U002617
 *
 */
public interface MailSender {

	/**
	 * send an eMail
	 * @param eMail
	 * @throws AddressException
	 * @throws MessagingException
	 * @throws IOException
	 */
	void send(MailBean eMail) throws Exception;

}
