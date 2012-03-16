package foo.beans;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import am.ajf.injection.annotation.Cached;



public class MyDefaultServiceImpl implements MyService {

	protected String stringTemplate;

	public MyDefaultServiceImpl() {
		super();
	}

	@PostConstruct
	public void init() {
		System.out.println("@PostConstruct");
	}
	
	@PreDestroy
	public void finalize() {
		System.out.println("@PreDestroy");
	}
	
	public String getStringTemplate() {
		return stringTemplate;
	}

	public void setStringTemplate(String stringTemplate) {
		this.stringTemplate = stringTemplate;
	}

	@Override
	@Cached
	public String doSomething() {
		System.out.println("Done.");
		return stringTemplate;
	}

	@Override
	public String toString() {
		return "MyDefaultServiceImpl [stringTemplate=" + stringTemplate + "]";
	}

}
