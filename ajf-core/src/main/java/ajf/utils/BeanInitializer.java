package ajf.utils;

public interface BeanInitializer {

	/**
	 * 
	 * @param instance
	 */
	public void initialize(Object instance);
	
	/**
	 * @return the default instance
	 */
	public BeanInitializer getInstance();
	
}
