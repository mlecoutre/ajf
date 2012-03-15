package am.ajf.core.beans;

public class MyDefaultServiceImpl  
	implements MyService {

	protected String stringTemplate;

	public MyDefaultServiceImpl() {
		super();
	}

	public String getStringTemplate() {
		return stringTemplate;
	}

	public void setStringTemplate(String stringTemplate) {
		this.stringTemplate = stringTemplate;
	}

	public String doSomething() {
		return stringTemplate.concat(".");
	}

}
