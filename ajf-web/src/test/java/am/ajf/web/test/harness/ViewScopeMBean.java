package am.ajf.web.test.harness;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;

@ViewScoped
public class ViewScopeMBean {
	
	public int counter;
	
	@PostConstruct
	public void init() {
		counter = 0;
	}
	
	public void increment() {
		counter++;
	}

	public int getCounter() {
		return counter;
	}
	
}
