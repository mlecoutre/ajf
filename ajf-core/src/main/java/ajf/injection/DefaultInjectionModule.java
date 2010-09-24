package ajf.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.matcher.Matchers;

public class DefaultInjectionModule extends AbstractModule {
	
	private static Module instance = new DefaultInjectionModule(); 
	
	private DefaultInjectionModule() {
		super();
	}
	
	public static Module getInstance() {
		return instance;
	}

	@Override
	protected void configure() {
		
		bindListener(Matchers.any(), ServicesTypeListener.getInstance());

	}
	
}
