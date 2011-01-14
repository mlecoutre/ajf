package ajf.injection;


import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * @author vincent dependencies injector library
 */
public abstract class DependenciesInjector {
	
	/**
	 * inject members
	 * 
	 * @param bean
	 */
	public static void inject(Object instance) {

		Injector injector = giveInjector(instance);
		injector.injectMembers(instance);
		
	}

	/**
	 * 
	 * @param instance
	 * @return
	 */
	public static Injector giveInjector(Object instance) {
		Injector injector = null;
		
		Class<?> objectClass = instance.getClass(); 
		
		Module module = retrieveInjectionModule(objectClass);
		if (null != module) {
			injector = InjectionContext.getInjector(module);
		}
		else {
			injector = InjectionContext.getInjector();
		}

		/*
		if ((null == modulesList) || (modulesList.isEmpty())) {
			throw new NullPointerException("Unable to find module");
		}
		*/	
		return injector;
	}
	
	/**
	 * 
	 * @param objectClass
	 * @return the required injection module
	 */
	public static Module retrieveInjectionModule(Class<?> objectClass) {
		Module module = null;

		// is the object class using annotation InjectionModule
		if (objectClass.isAnnotationPresent(InjectionModule.class)) {
			
			InjectionModule injectionModuleAnnotation = objectClass
				.getAnnotation(InjectionModule.class);

			// get the injection module class
			Class<? extends Module> moduleClass = injectionModuleAnnotation
					.value();
			try {
				// instantiate the injection module
				module = (Module) moduleClass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
						
		return module;
	}

}
