package ajf.injection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public abstract class InjectionContext {

	private static final Module defaultInjectionModule = DefaultInjectionModule
			.getInstance();
	
	private static List<Module> injectionModulesList;
	private static List<Module> readOnlyInjectionModulesList;
	
	private static Injector injector; 

	static {
		injectionModulesList = new ArrayList<Module>();
		addInjectionModule(defaultInjectionModule);
	}
	
	public static Injector getInjector() {
		return injector;
	}
	
	public static Injector getInjector(Module module) {
		
		List<Module> modules = new ArrayList<Module>();
		if (null != injectionModulesList)
			modules.addAll(injectionModulesList);
		modules.add(module);
		
		// generate the Guice Injector
		Injector myInjector = injector = Guice.createInjector(modules);
		return myInjector;
	}

	/**
	 * @return the injectionModules list
	 */
	public static List<Module> getInjectionModules() {
		return readOnlyInjectionModulesList;
	}

	/**
	 * @param injectionModule
	 *            to add
	 */
	public static void addInjectionModule(Module module) {
		injectionModulesList.add(module);
		updateInternals();
	}

	/**
	 * @param injectionModule
	 *            to remove
	 */
	public static void removeInjectionModule(Module module) {
		injectionModulesList.remove(module);
		updateInternals();
	}

	private static void updateInternals() {
		// synchronize the ReadOnly collection
		readOnlyInjectionModulesList = Collections.unmodifiableList(injectionModulesList);
		// generate the Guice Injector
		injector = Guice.createInjector(injectionModulesList);		
	}

	/**
	 * reset the injectionModules list
	 */
	public static void resetInjectionModules() {
		injectionModulesList.clear();
		addInjectionModule(defaultInjectionModule);
	}

}
