package ajf.persistence.jpa;

import javax.inject.Named;

@Named
public class SimpleInjectedBean {

	public String doSomething() {
		return "test in SimpleInjectedBean";
	}
	
}
