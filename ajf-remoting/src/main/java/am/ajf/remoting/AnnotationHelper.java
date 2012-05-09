package am.ajf.remoting;

import java.lang.reflect.Method;

import am.ajf.remoting.Remote;

public class AnnotationHelper {
	
	public static String getJndiInfo(Method method, Class<?> interfaceClass) throws ConfigurationException {
		//Remote remote = method.getDeclaringClass().getAnnotation(Remote.class);
		Remote remote = interfaceClass.getAnnotation(Remote.class);
		if (method.isAnnotationPresent(Remote.class)) {
			remote = method.getAnnotation(Remote.class);
		}
		if (remote == null) {
			throw new ConfigurationException("Annotation @Remote not found on method or class containing method : "+method.getName());
		}		
		//TODO handle the array of jndi for cases like JMS that that need multiples JNDI entry points
		return remote.jndi();
	}

}
