package am.ajf.remoting.ejb.impl;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class RemoteEJBHelper {
	
	private static final String IC_FACTORY_IBM = "com.ibm.websphere.naming.WsnInitialContextFactory";
	private static final String IC_FACTORY_SUN = "com.sun.jndi.cosnaming.CNCtxFactory";
	private static final String SYS_PROP_JAVA_VM_VENDOR = "java.vm.vendor";
	private static final String VENDOR_IBM_CORPORATION = "IBM Corporation";
	private static final String VENDOR_SUN_MICROSYSTEMS_INC = "Sun Microsystems Inc.";

	//This should be automagically initialized on the first get call.
	private static String namingFactory;
	
	private static ConcurrentHashMap<String, String> localJndiCache = new ConcurrentHashMap<String, String>();
	
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
	 * @param jndi : jndi name of the host url concat with the remote ejb jndi 
	 * @return
	 * @throws NamingException
	 */
	public static Object getEJBRef(String jndi) throws NamingException {		
			//retrieve info from the local jndi tree
			//we cache the result indefinitely as the local jndi tree
			//can only change if the server is restarted
			String remoteJndi = localJndiCache.get(jndi);
			if (remoteJndi == null) {
				Context ctxLocal = new InitialContext();
				remoteJndi = (String) ctxLocal.lookup(jndi);
				localJndiCache.put(jndi, remoteJndi);
			}
			
			//look for the "/" after the "iiop://" 
			int splitIndex = remoteJndi.indexOf("/", 8); 
			String host = remoteJndi.substring(0, splitIndex);
			String ejbJndi = remoteJndi.substring(splitIndex+1);
			
			
			//connect to the distant jndi tree (this will take more time)
			Properties properties = new Properties();		
			properties.put(Context.INITIAL_CONTEXT_FACTORY, getNamingFactory());		
	        properties.put(Context.PROVIDER_URL, host);
		
			Context ctx = new InitialContext(properties);
			
			//Get the ref to the remote EJB instance			
			Object nRemoteObj = ctx.lookup(ejbJndi);
			
			return nRemoteObj;
	}
	
	/**
	 * Choose the naming factory based on the JVM vendor.
	 * The application server name and version should not matter.
	 *  
	 * @return initial context factory class name
	 */
	public static String getNamingFactory() {
		if (namingFactory == null) {
			if (VENDOR_SUN_MICROSYSTEMS_INC.equals(System.getProperty(SYS_PROP_JAVA_VM_VENDOR))) {
				namingFactory = IC_FACTORY_SUN;
			} else if (VENDOR_IBM_CORPORATION.equals(System.getProperty(SYS_PROP_JAVA_VM_VENDOR))) {
				namingFactory = IC_FACTORY_IBM;
			}
		}
		return namingFactory;		
	}

	/** 
	 * Force the naming factory to use for remote call.
	 * The naming factory should not be set manually unless :
	 * a) You unit test the framework
	 * b) the autodetection is not working on your use case.
	 * 
	 * @param namingFactory
	 */
	public static void setNamingFactory(String namingFactory) {
		RemoteEJBHelper.namingFactory = namingFactory;
	}

}
