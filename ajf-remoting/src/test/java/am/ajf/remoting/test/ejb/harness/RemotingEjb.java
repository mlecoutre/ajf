package am.ajf.remoting.test.ejb.harness;

public class RemotingEjb implements RemotingEjbRemote {

	public void emptyRes() {
		System.out.println("empty res !");
	}

	@Override
	public String simpleTypeRes() {		
		return "res";
	}

	@Override
	public Model complexTypeRes() {		
		return new Model("res");
	}

	@Override
	public String resWithParam(int p1, int p2) {
		return "res("+p1+","+p2+")";
	}
	
}
