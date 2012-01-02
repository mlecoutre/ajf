package ajf.persistence.jpa.test.harness;

import javax.inject.Named;

@Named
public class SimpleInjectedBean {

	public String doSomething() {
		return "test in SimpleInjectedBean";
	}
	
}
