package ajf.mail;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public interface MailSender {

	/**
	 * send an eMail
	 * @param eMail
	 * @throws AddressException
	 * @throws MessagingException
	 * @throws IOException
	 */
	void send(MailBean eMail) throws AddressException, MessagingException,
			IOException;

}
