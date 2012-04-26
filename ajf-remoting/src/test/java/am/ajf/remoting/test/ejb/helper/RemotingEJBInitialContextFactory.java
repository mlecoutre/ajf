package am.ajf.remoting.test.ejb.helper;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import bitronix.tm.jndi.BitronixContext;

public class RemotingEJBInitialContextFactory implements InitialContextFactory {

	@Override
	public Context getInitialContext(Hashtable<?, ?> arg0)
			throws NamingException {
		return new RemotingEJBContext();
	}

}
