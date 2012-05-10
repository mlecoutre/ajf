package foo.core.services;

import javax.ejb.Stateless;

import foo.lib.services.MyEjbServiceBD;

@Stateless
public class MyEjbService implements MyEjbServiceBD {

	private String createdBy;
	
	public MyEjbService() {
		this.createdBy = "CDI";
	}
	
	public MyEjbService(String createdBy) {
		this.createdBy = createdBy;
	}
	
	@Override
	public String hello(String who) {		
		return String.format("Hello %s !", who);
	}

	@Override
	public String getCreatedBy() {
		return createdBy;
	}
	
}
