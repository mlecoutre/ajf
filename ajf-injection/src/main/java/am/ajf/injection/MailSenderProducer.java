package am.ajf.injection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.commons.configuration.Configuration;

import am.ajf.core.ApplicationContext;
import am.ajf.core.mail.MailSender;
import am.ajf.core.mail.impl.SimpleMailSenderImpl;

import com.google.common.base.Strings;

@ApplicationScoped
public class MailSenderProducer {
	
	private static final String MAIL_SESSION_JNDI_NAME = "mail/Session";
	private static final String MAIL_SENDER_JNDI_NAME_PROPERTY = "mailSender.jndiName";
	
	public MailSenderProducer() {
		super();
	}
	
	@Produces
	public MailSender produceMailSender(InjectionPoint ip) {
		
		Configuration config = ApplicationContext.getConfiguration();
		String jndiName = config.getString(MAIL_SENDER_JNDI_NAME_PROPERTY, MAIL_SESSION_JNDI_NAME);
		if (Strings.isNullOrEmpty(jndiName)) {
			String who = ip.getMember().getDeclaringClass().getName()
				.concat("#").concat(ip.getMember().getName());
			
			throw new RuntimeException("The field injection for '".concat(who)
					.concat("' can not be done because the property '")
					.concat(MAIL_SENDER_JNDI_NAME_PROPERTY)
					.concat("' is null or empty."));
		}
		
		SimpleMailSenderImpl mailSenderImpl = new SimpleMailSenderImpl();
		mailSenderImpl.setJndiName(jndiName);
		
		return mailSenderImpl;
		
	}
	

}
