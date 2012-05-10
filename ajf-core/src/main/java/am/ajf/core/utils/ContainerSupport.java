package am.ajf.core.utils;

/**
 * This class provide methods to test a container capabilities.
 * This is necessary because ajf is targeted into a variety of
 * environments and they dont all have the same class loaded, therefore
 * to avoid ClassNotFoundException you need to test the container
 * capability before you can work on optional classes.
 * 
 * Current optional capabilities are :
 * - EJBs  
 * 
 * @author Nicolas Radde (E016696)
 *
 */
public class ContainerSupport {

	private static Boolean ejbSupport;
	
	/**
	 * Test the ejb support.
	 * 
	 * @return
	 */
	public static boolean isEJBSupported() {
		if (ejbSupport == null) {
			try {
				Class.forName("javax.ejb.Stateless");
				ejbSupport = Boolean.TRUE;
			} catch (ClassNotFoundException e) {				
				ejbSupport = Boolean.FALSE;
			}
		}		
		return ejbSupport.booleanValue();
	}
	
	
	
}
