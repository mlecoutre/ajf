package am.ajf.injection;

//import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import am.ajf.core.beans.BeansManager;
import am.ajf.core.mail.MailSender;

public class MailSenderProducer {
	
	/*
	private static final String DEFAULT_MAIL_SESSION_JNDI_NAME = "mail/Session";
	private static final String MAIL_SENDER_JNDI_NAME_PROPERTY = "mailSender.jndiName";
	*/
	
	public MailSenderProducer() {
		super();
	}
	
	@Produces
	public MailSender produceMailSender(InjectionPoint ip) throws Exception {
		
		/*
		Configuration config = ApplicationContext.getConfiguration();
		String jndiName = config.getString(MAIL_SENDER_JNDI_NAME_PROPERTY, DEFAULT_MAIL_SESSION_JNDI_NAME);
		if (Strings.isNullOrEmpty(jndiName)) {
			String who = ip.getMember().getDeclaringClass().getName()
				.concat("#").concat(ip.getMember().getName());
			
			throw new RuntimeException("The field injection for '".concat(who)
					.concat("' can not be done because the property '")
					.concat(MAIL_SENDER_JNDI_NAME_PROPERTY)
					.concat("' is null or empty."));
		}
		*/
		
		MailSender mailSender = BeansManager.getDefaultBean(MailSender.class);
		return mailSender;
		
	}
	

}
