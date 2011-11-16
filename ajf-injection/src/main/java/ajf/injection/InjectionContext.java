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

	private static final DefaultInjectionModule DEFAULT_INJECTION_MODULE = new DefaultInjectionModule();
	// private static ConfigurationInjectionModule configurationInjectionModule = null;

	private final static Logger logger = LoggerFactory.getLogger(InjectionContext.class);
	
	private final static InjectionContext instance = new InjectionContext();
	
	private List<Module> injectionModulesList = new ArrayList<Module>();
	private Injector injector = null; 
	
	private InjectionContext() {
		super();
		
		/*
		try {
			Configuration configuration = ApplicationContext.getConfiguration();
			configurationInjectionModule = new ConfigurationInjectionModule(configuration);
		}
		catch (Exception e) {
			logger.warn("Receive exception while trying to initialize 'ApplicationContext'.", e);
		}		
		*/
		
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
			Module[] modules = new Module[] {DEFAULT_INJECTION_MODULE};
			// , configurationInjectionModule};
			injector = Guice.createInjector(Stage.PRODUCTION, modules);	
		}
		else {
			Module[] modules = new Module[1+injectionModulesList.size()]; // 1+
			modules[0] = DEFAULT_INJECTION_MODULE;
			//modules[1] = configurationInjectionModule;
			int id = 0; // 1
			for (Module module : injectionModulesList) {
				id++;
				modules[id] = module;		
			}
			injector = Guice.createInjector(Stage.PRODUCTION, modules);
		}
	}

	/**
	 * reset the injectionModules list
	 */
	public void resetInjectionModules() {
		injectionModulesList.clear();
		addInjectionModule(DEFAULT_INJECTION_MODULE);
		//addInjectionModule(configurationInjectionModule);
	}

}
