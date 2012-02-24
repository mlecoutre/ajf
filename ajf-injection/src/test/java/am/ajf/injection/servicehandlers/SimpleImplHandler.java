package am.ajf.injection.servicehandlers;

import java.lang.reflect.Method;
import java.util.List;

import am.ajf.injection.ImplementationHandler;

public class SimpleImplHandler implements ImplementationHandler {

	@Override
	public boolean canHandle(Method method) {		
		return "doSomething".equals(method.getName());
	}

	@Override
	public Class<?> implementMethodsFor(Class<?> superClass, Class<?> interfaceClass,
			List<Method> methods) {
		return SimpleServiceDoSomething.class;
	}
	
	//Helper class
	public class SimpleServiceDoSomething implements SimpleServiceBD {		
		public String doSomething() {
			return "result";
		}
	};

}
