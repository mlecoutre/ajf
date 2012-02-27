package am.ajf.remoting;

import java.util.Map;

/**
 * Mapper to map an array of Object to a java type.
 * The Mapper implementation will use different strategies to chose
 * how to map the data to the java type.
 * 
 * @author Nicolas Radde (E016696)
 *
 * @param 
 */
public interface Mapper {
	
	Object map(Map<String, Object> data);

}
