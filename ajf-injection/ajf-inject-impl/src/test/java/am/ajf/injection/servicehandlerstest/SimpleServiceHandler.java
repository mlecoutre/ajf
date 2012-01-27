package am.ajf.injection.servicehandlerstest;

import java.lang.reflect.Method;
import java.util.List;

import am.ajf.injection.ServiceHandler;

public class SimpleServiceHandler implements ServiceHandler {

	@Override
	public boolean canHandle(Method method) {		
		return "doSomething".equals(method.getName());
	}

	@Override
	public Class<?> implementMethodsFor(Class<?> superClass,
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
