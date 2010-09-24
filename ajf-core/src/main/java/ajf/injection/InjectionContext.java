package ajf.injection;

import com.google.inject.Module;

public abstract class InjectionContext {

	private static Module injectionModule = DefaultInjectionModule.getInstance();
		
	/**
	 * @return the injectionModule
	 */
	public static Module getInjectionModule() {
		return injectionModule;
	}

	/**
	 * @param injectionModule the injectionModule to set
	 */
	public static void setInjectionModule(Module module) {
		injectionModule = module;
	}

	
	
	
}
