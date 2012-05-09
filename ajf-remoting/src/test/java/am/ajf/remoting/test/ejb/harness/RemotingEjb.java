package am.ajf.remoting.test.ejb.harness;

import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class RemotingEjb implements RemotingEjbRemote {

	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void emptyRes() {
		logger.info("empty res !");
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
