package ajf.injection;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import ajf.logger.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

public class InjectionContext {

	private final static Logger logger = LoggerFactory.getLogger(InjectionContext.class);
	
	private final static InjectionContext instance = new InjectionContext();
	
	private List<Module> injectionModulesList = new ArrayList<Module>();
	private Injector injector = null; 
	
	public InjectionContext() {
		super();
		updateInjector();
	}

	public static InjectionContext getInstance() {
		return instance;
	}

	public Injector getInjector() {
		return injector;
	}

	/**
	 * @return the injectionModules list
	 */
	public List<Module> getInjectionModules() {
		return injectionModulesList;
	}

	/**
	 * @param injectionModule
	 *            to add
	 */
	public void addInjectionModule(Module module) {
		logger.info("Add InjectionModule instance of: ".concat(module.getClass().getName()));
		injectionModulesList.add(module);
		updateInjector();
	}

	/**
	 * @param injectionModule
	 *            to remove
	 */
	public void removeInjectionModule(Module module) {
		injectionModulesList.remove(module);
		updateInjector();
	}

	private void updateInjector() {
		// generate the Guice Injector
		logger.info("Update Injector.");
		if (injectionModulesList.isEmpty()) {
			injector = Guice.createInjector(Stage.PRODUCTION, new DefaultInjectionModule());	
		}
		else {
			injector = Guice.createInjector(Stage.PRODUCTION, new DefaultInjectionModule(injectionModulesList));
		}
	}

	/**
	 * reset the injectionModules list
	 */
	public void resetInjectionModules() {
		injectionModulesList.clear();
		addInjectionModule(new DefaultInjectionModule());
	}

}
