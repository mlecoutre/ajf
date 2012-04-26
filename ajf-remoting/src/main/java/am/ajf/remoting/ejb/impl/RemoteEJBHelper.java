package am.ajf.remoting.ejb.impl;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class RemoteEJBHelper {
	
	// 1 is SUN
	// 2 is IBM
	// 0 is undefined 
	private static int containerType = 0; 
	
	//Sample configurations that can be used for Unit tests
	//properties.put("java.naming.factory.initial","bitronix.tm.jndi.BitronixInitialContextFactory");		
    //properties.put(Context.INITIAL_CONTEXT_FACTORY,"org.apache.naming.java.javaURLContextFactory"); 
    //properties.put(Context.URL_PKG_PREFIXES,"org.apache.naming"); 
    //properties.put(Context.URL_PKG_PREFIXES,"java:org.apache.naming"); 
	//properties.put("java.naming.provider.url", "iiop://localhost:2817");
	//properties.put("org.omg.CORBA.ORBInitialHost","localhost"); 
	//properties.put("org.omg.CORBA.ORBInitialPort","2817");
	//properties.put(Context.PROVIDER_URL, "corbaname:iiop:localhost:2817/NameServiceServerRoot");
	//Standard form of a remote ejb raw jndi in was7
	//Object nRemoteObj = ctx.lookup("ejb/RemoteEAR/RemoteEJB\\.jar/Nickho#am\\.test\\.remote\\.NickhoRemote");
	
	
	/**
	 * Contacts the differents JNDI servers to get the ref to the EJB remote.
	 * TODO : Caching of lookups !
	 * 
	 * @param host : jndi name of the host url 
	 * @param jndi : remote jndi name of the EJB
	 * @return
	 * @throws NamingException
	 */
	public static Object getEJBRef(String host, String jndi) throws NamingException {		
			//retrieve info from the local jndi tree
			Context ctxLocal = new InitialContext();
			String hostURL = (String) ctxLocal.lookup(host);
			
			//connect to the distant jndi tree (this will take more time)
			Properties properties = new Properties();		
			properties.put(Context.INITIAL_CONTEXT_FACTORY, getNamingFactory());		
	        properties.put(Context.PROVIDER_URL, hostURL);
		
			Context ctx = new InitialContext(properties);
			
			//Get the ref to the remote EJB instance			
			Object nRemoteObj = ctx.lookup(jndi);
			
			return nRemoteObj;
	}
	
	/**
	 * Choose the naming factory based on the JVM vendor.
	 * The application server name and version should not matter.
	 *  
	 * @return
	 */
	private static String getNamingFactory() {
		if (containerType == 0) {
			if ("Sun Microsystems Inc.".equals(System.getProperty("java.vm.vendor"))) {
				containerType = 1;
			} else if ("IBM Corporation".equals(System.getProperty("java.vm.vendor"))) {
				containerType = 2;
			}
		}
		
		switch (containerType) {
			case 1 : return "com.sun.jndi.cosnaming.CNCtxFactory";
			case 2 : return "com.ibm.websphere.naming.WsnInitialContextFactory";
			default : throw new UnsupportedOperationException("EJB remoting only work on SUN and IBM JREs");
		}
	}
	

}
