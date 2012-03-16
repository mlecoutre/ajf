package am.ajf.injection.beans;

import am.ajf.core.mail.MailBean;
import am.ajf.core.mail.impl.SimpleMailSenderImpl;

public class AnotherMailSenderImpl extends SimpleMailSenderImpl {

	public AnotherMailSenderImpl() {
		super();
	}

	@Override
	public void send(MailBean eMail) {
		System.out.println("Do nothing");		
	}

	@Override
	public String toString() {
		return "AnotherMailSenderImpl []";
	}
	
	
	
}
