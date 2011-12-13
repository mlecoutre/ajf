package ajf.persistence.jpa;

public interface ClassMatcher {
	
	public boolean isServiceClass(Class<?> clazz);
	public boolean isServiceInterface(Class<?> clazz);

}
