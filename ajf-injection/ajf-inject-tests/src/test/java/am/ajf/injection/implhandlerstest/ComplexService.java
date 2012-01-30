package am.ajf.injection.implhandlerstest;

public abstract class ComplexService implements ComplexServiceBD {

	@Override
	public String doSomethingManually() {		
		return "result-3";
	}

}
