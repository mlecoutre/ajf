package am.ajf.injection;

public interface KeyBuilder {

	/**
	 * 
	 * @param cachedAnnotation
	 * @param parameters
	 * @return a key as cache entry
	 */
	Object build(Cached cachedAnnotation, Object[] parameters); 
	
}
