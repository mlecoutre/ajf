package ajf.injection;


import ajf.injection.annotations.InjectionModule;

import com.google.inject.Guice;
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

		Injector injector = getInjector(instance);
		injector.injectMembers(instance);

		// build and inject mocks if required 
		// MockitoAnnotations.initMocks(instance);
		
	}

	/**
	 * 
	 * @param instance
	 * @return
	 */
	public static Injector getInjector(Object instance) {
		Class<?> objectClass = instance.getClass(); 
		Module module = retrieveInjectionModule(objectClass);
		
		if (null == module) {
			throw new NullPointerException("Unable to find module");
		}
		
		// create and use the guice injector
		Injector injector = Guice.createInjector(module);
		return injector;
	}
	
	/**
	 * 
	 * @param objectClass
	 * @return the required injection module
	 */
	private static Module retrieveInjectionModule(Class<?> objectClass) {
		Module module = null;

		// is the object class using annotation InjectionModule
		if (objectClass.isAnnotationPresent(InjectionModule.class)) {
			
			InjectionModule injectionModuleAnnotation = objectClass
				.getAnnotation(InjectionModule.class);

			// get the injection module class
			Class<? extends Module> moduleClass = injectionModuleAnnotation
					.value();
			try {
				// instanciate the injection module
				module = (Module) instanciate(moduleClass);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		if (null == module) {
			module = InjectionContext.getInjectionModule();
		}
						
		return module;
	}

	/**
	 * inject members of a specified class
	 * @param <T>
	 * @param type
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T inject(Class<?> type) throws InstantiationException,
			IllegalAccessException {

		Object bean = instanciate(type);
		return (T) bean;
	
	}

}
