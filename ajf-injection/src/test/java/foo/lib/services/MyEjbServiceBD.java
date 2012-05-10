package foo.lib.services;

import javax.ejb.Local;

@Local
public interface MyEjbServiceBD {
	
	public String hello(String who);
	public String getCreatedBy();
	
}
