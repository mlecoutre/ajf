package ajf.injection;

import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.matcher.Matchers;

public class DefaultInjectionModule extends AbstractModule {
	
	private List<Module> additionalModules = null;
	
	public DefaultInjectionModule() {
		super();
	}
	
	public DefaultInjectionModule(List<Module> modules) {
		super();
		this.additionalModules = modules;
	}
		
	@Override
	protected void configure() {
		
		bindListener(Matchers.any(), new InjectionTypeListener());
		
		if ((null != additionalModules) && (!additionalModules.isEmpty())) {
			for (Module module : additionalModules) {
				install(module);	
			}
		}
			
		
	}
	
}
