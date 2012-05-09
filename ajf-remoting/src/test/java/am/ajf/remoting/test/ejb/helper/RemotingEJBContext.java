package am.ajf.remoting.test.ejb.helper;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.remoting.test.ejb.harness.RemotingEjb;
import bitronix.tm.jndi.BitronixContext;
import bitronix.tm.jndi.BitronixInitialContextFactory;

/**
 * Mocking delegate as i didnt found an easier way to declare 
 * jndi variables without breaking bitronix/tomcat base jndi.
 * 
 * @author Nicolas Radde (E016696)
 */
public class RemotingEJBContext implements Context {	

	private BitronixContext delegate;
	private final transient Logger logger = LoggerFactory.getLogger(this.getClass()); 
	
	public RemotingEJBContext() {
		delegate = new BitronixContext();
	}

	public void close() throws NamingException {
		delegate.close();
	}

	public Object lookup(Name name) throws NamingException {
		return delegate.lookup(name);
	}

	/**
	 * The only method that is actually use.
	 * Add here all the jndi entry you wants.
	 */
	public Object lookup(String s) throws NamingException {
		if ("url/ejb_remote_remotingejb".equals(s)) {
			String localURL = "iiop://localhost:2809/ejb/RemoteEAR/RemoteEJB\\.jar/RemotingEJBRemote#am\\.ajf\\.remoting\\.test\\.ejb\\.harness\\.RemotingEjbRemote";
			logger.info("Mocked jndi : "+localURL);
			return localURL;
		} else if ("ejb/RemoteEAR/RemoteEJB\\.jar/RemotingEJBRemote#am\\.ajf\\.remoting\\.test\\.ejb\\.harness\\.RemotingEjbRemote".equals(s)) {
			logger.info("Mocked jndi : manual instance of object=>RemotingEjb()");
			return new RemotingEjb();
		}
		return delegate.lookup(s);
	}

	public void bind(Name name, Object o) throws NamingException {
		delegate.bind(name, o);
	}

	public void bind(String s, Object o) throws NamingException {
		delegate.bind(s, o);
	}

	public void rebind(Name name, Object o) throws NamingException {
		delegate.rebind(name, o);
	}

	public void rebind(String s, Object o) throws NamingException {
		delegate.rebind(s, o);
	}

	public void unbind(Name name) throws NamingException {
		delegate.unbind(name);
	}

	public void unbind(String s) throws NamingException {
		delegate.unbind(s);
	}

	public void rename(Name name, Name name1) throws NamingException {
		delegate.rename(name, name1);
	}

	public void rename(String s, String s1) throws NamingException {
		delegate.rename(s, s1);
	}

	public NamingEnumeration list(Name name) throws NamingException {
		return delegate.list(name);
	}

	public NamingEnumeration list(String s) throws NamingException {
		return delegate.list(s);
	}

	public NamingEnumeration listBindings(Name name) throws NamingException {
		return delegate.listBindings(name);
	}

	public NamingEnumeration listBindings(String s) throws NamingException {
		return delegate.listBindings(s);
	}

	public void destroySubcontext(Name name) throws NamingException {
		delegate.destroySubcontext(name);
	}

	public void destroySubcontext(String s) throws NamingException {
		delegate.destroySubcontext(s);
	}

	public Context createSubcontext(Name name) throws NamingException {
		return delegate.createSubcontext(name);
	}

	public Context createSubcontext(String s) throws NamingException {
		return delegate.createSubcontext(s);
	}

	public Object lookupLink(Name name) throws NamingException {
		return delegate.lookupLink(name);
	}

	public Object lookupLink(String s) throws NamingException {
		return delegate.lookupLink(s);
	}

	public NameParser getNameParser(Name name) throws NamingException {
		return delegate.getNameParser(name);
	}

	public NameParser getNameParser(String s) throws NamingException {
		return delegate.getNameParser(s);
	}

	public Name composeName(Name name, Name name1) throws NamingException {
		return delegate.composeName(name, name1);
	}

	public String composeName(String s, String s1) throws NamingException {
		return delegate.composeName(s, s1);
	}

	public Object addToEnvironment(String s, Object o) throws NamingException {
		return delegate.addToEnvironment(s, o);
	}

	public Object removeFromEnvironment(String s) throws NamingException {
		return delegate.removeFromEnvironment(s);
	}

	public Hashtable getEnvironment() throws NamingException {
		return delegate.getEnvironment();
	}

	public String getNameInNamespace() throws NamingException {
		return delegate.getNameInNamespace();
	}

}
