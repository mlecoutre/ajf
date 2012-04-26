package am.ajf.remoting.test.ejb.harness;

import am.ajf.remoting.Remote;
import am.ajf.remoting.ejb.annotation.RemoteEJB;

@Remote(jndi = "url/host")
public interface RemoteEJBServiceBD {
	
	@RemoteEJB(value = "ejb/RemotingEJB", name=RemotingEjbRemote.class)
	public void emptyRes();
	
	@RemoteEJB(value = "ejb/RemotingEJB", name=RemotingEjbRemote.class)
	public String simpleTypeRes();
	
	@RemoteEJB(value = "ejb/RemotingEJB", name=RemotingEjbRemote.class)
	public Model complexTypeRes();

	@RemoteEJB(value = "ejb/RemotingEJB", name=RemotingEjbRemote.class)
	public String resWithParam(int p1, int p2);
	
}
