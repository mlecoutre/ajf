package am.ajf.injection;

import java.lang.reflect.Method;
import java.util.List;

public interface ImplementationHandler {
	
	boolean canHandle(Method method);

	Class<?> implementMethodsFor(Class<?> superClass, Class<?> interfaceClass, List<Method> methods);
	
}
