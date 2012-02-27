package am.ajf.remoting;

/**
 * Exception on client configuration error in using ajf-remoting
 * 
 * @author Nicolas Radde (E016696)
 */
public class ConfigurationException extends Exception {

	private static final long serialVersionUID = 3419820695035890835L;
	
	public ConfigurationException(Exception ex) {
		super(ex);
	}
	
	public ConfigurationException(String message) {
		super(message);
	}

}
