package am.ajf.persistence.jpa.test.harness;

import javax.inject.Inject;

public class SimpleInjectedClass {

	private @Inject SimpleInjectedBean sib;
	
	public SimpleInjectedClass() {		
	}
	
	public String doSomethingOnInjected() {
		return sib.doSomething();
	}
	
	
}
