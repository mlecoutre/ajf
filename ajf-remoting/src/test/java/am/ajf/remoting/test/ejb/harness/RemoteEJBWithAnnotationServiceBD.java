package am.ajf.remoting.test.ejb.harness;

import am.ajf.remoting.Remote;
import am.ajf.remoting.ejb.annotation.RemoteEJB;

@Remote(jndi = "url/ejb_remote_remotingejb")
public interface RemoteEJBWithAnnotationServiceBD {
	
	@RemoteEJB
	public String resWithParam(int p1, int p2);
	
}
