package am.ajf.web.servlets;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SendMailServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SendMailServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		try {
			Context initCtx = new InitialContext();
			Object bindedObject = initCtx.lookup("java:comp/env/mail/Session");
			Session session = (Session) bindedObject;
			//Session session = (Session) initCtx.lookup("mail/Session");

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("test@arcelormittal.com"));
			InternetAddress to[] = new InternetAddress[1];
			to[0] = new InternetAddress("vincent.claeysen@arcelormittal.com");
			message.setRecipients(Message.RecipientType.TO, to);
			message.setSubject("test");
			message.setContent("OK", "text/plain");
			
			Transport.send(message);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		
	}
	
	

}
