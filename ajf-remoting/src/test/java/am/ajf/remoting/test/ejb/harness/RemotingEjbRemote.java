package am.ajf.remoting.test.ejb.harness;


public interface RemotingEjbRemote {

	public void emptyRes();
	public String simpleTypeRes();
	public Model complexTypeRes();	
	public String resWithParam(int p1, int p2);
	
}
