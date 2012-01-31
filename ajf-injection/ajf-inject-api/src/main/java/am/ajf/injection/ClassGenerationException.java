package am.ajf.injection;

/**
 * Represent an error that can occur generating a class.
 * 
 * @author Nicolas Radde (E016696)
 *
 */
public class ClassGenerationException extends Exception {

	private static final long serialVersionUID = -3402129326052075631L;

	public ClassGenerationException(String message, Exception ex) {
		super(message, ex);
	}
	
	public ClassGenerationException(Exception ex) {
		super(ex);
	}
	
	public ClassGenerationException(String message) {
		super(message);
	}
		
	public ClassGenerationException() {
		super();
	}
}
