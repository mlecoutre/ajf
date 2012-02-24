package am.ajf.injection.implhandlers;

public abstract class ComplexService implements ComplexServiceBD {

	@Override
	public String doSomethingManually() {		
		return "result-3";
	}

}
