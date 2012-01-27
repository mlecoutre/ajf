package am.ajf.injection;

import java.lang.reflect.Method;
import java.util.List;

public interface ServiceHandler {
	
	boolean canHandle(Method method);

	Class<?> implementMethodsFor(Class<?> superClass, List<Method> methods);
	
}
