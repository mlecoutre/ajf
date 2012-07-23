package foo.beans;

//import javax.enterprise.inject.Alternative;

import javax.enterprise.inject.Alternative;

import am.ajf.core.Service;
import am.ajf.injection.annotation.Monitored;

/**
 * @author U002617
 *
 */
@Alternative 
public class AnOtherServiceImpl implements MyService, Service {

	protected String stringTemplate;

	public AnOtherServiceImpl() {
		super();
	}

	public String getStringTemplate() {
		return stringTemplate;
	}

	public void setStringTemplate(String stringTemplate) {
		this.stringTemplate = stringTemplate;
	}

	@Override
	@Monitored
	public String doSomething() {
		System.out.println("Another Done.");
		return stringTemplate;
	}

	@Override
	public String toString() {
		return "AnOtherServiceImpl [stringTemplate=" + stringTemplate + "]";
	}

	@Override
	public void start() {
		System.out.println("Start");
	}

	@Override
	public void stop() {
		System.out.println("Stop");		
	}

}
