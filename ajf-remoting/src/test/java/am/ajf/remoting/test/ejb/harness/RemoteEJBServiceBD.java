package am.ajf.remoting.test.ejb.harness;

import am.ajf.remoting.Remote;

@Remote(jndi = "url/ejb_remote_remotingejb")
public interface RemoteEJBServiceBD extends RemotingEjbRemote {}
