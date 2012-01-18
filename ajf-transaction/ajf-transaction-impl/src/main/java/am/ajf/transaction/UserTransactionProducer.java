package am.ajf.transaction;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

public class UserTransactionProducer {
	
	@Produces
	@RequestScoped
	public UserTransaction produceUserTransaction() throws NamingException {
		InitialContext ic = new InitialContext();
		UserTransaction ut = (UserTransaction) ic.lookup("java:comp/UserTransaction");
		return ut;
	}	

}
