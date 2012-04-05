package am.ajf.remoting.test.procs.harness;

import java.util.List;

import am.ajf.remoting.procs.annotation.Out;
import am.ajf.remoting.procs.annotation.Result;

/**
 * Sample Wrapper object to test @Result and @Out 
 * 
 * @author Nicolas Radde (E016696)
 *
 */
public class FindModelResult {
	
	@Result
	private List<ModelSp> result;
	
	@Out("STATUS")
	private String status;

	public List<ModelSp> getResult() {
		return result;
	}

	public void setResult(List<ModelSp> result) {
		this.result = result;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
