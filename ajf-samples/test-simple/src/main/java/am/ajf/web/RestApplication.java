package am.ajf.web;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import am.ajf.web.resources.HelloWorldsResource;

public class RestApplication extends Application {

	public RestApplication() {
		super();
	}

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> serviceClasses = new HashSet<Class<?>>();
		serviceClasses.add(HelloWorldsResource.class);
		return serviceClasses;
	}
	
}
